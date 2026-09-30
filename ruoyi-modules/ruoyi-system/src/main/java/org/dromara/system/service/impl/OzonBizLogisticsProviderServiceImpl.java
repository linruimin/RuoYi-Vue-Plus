package org.dromara.system.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.system.domain.OzonBizLogisticsProvider;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizLogisticsProviderVo;
import org.dromara.system.mapper.OzonBizLogisticsProviderMapper;
import org.dromara.system.service.IOzonBizLogisticsProviderService;
/** 物流商资料事务管理与关联校验。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizLogisticsProviderServiceImpl implements IOzonBizLogisticsProviderService {
 private final OzonBizLogisticsProviderMapper mapper;
 private final OzonBusinessSupport support;
 public PageResult<OzonBizLogisticsProviderVo> queryPageList(OzonBusinessQuery q,PageQuery p) {
  Page<OzonBizLogisticsProviderVo> page=mapper.selectBusinessPage(new Page<>(Math.max(1,p.getPageNum()),Math.min(100,Math.max(1,p.getPageSize()))),support.<OzonBizLogisticsProvider>query("logistics_provider",q,p));
  for(var row:page.getRecords()) enrich(row);
  return PageResult.build(page.getRecords(),page.getTotal());
 }
 private void enrich(OzonBizLogisticsProviderVo row) { row.setRevision(support.revision("logistics_provider",row.getId()));  }
 @Transactional(rollbackFor=Exception.class)
 public OzonBizLogisticsProviderVo queryById(Long id) {
  support.lockForRead("logistics_provider",id);
  var rows=mapper.selectBusinessPage(new Page<>(1,1),new QueryWrapper<OzonBizLogisticsProvider>().eq("b.id",id)).getRecords();
  if(rows.isEmpty()) throw new ServiceException("记录不存在或已删除");
  var row=rows.getFirst();enrich(row);return row;
 }
 @Transactional(rollbackFor=Exception.class)
 public Long insertByBo(OzonBizLogisticsProviderBo bo) {
  var entity=MapstructUtils.convert(bo,OzonBizLogisticsProvider.class);entity.setId(null); entity.setFeishuRecordId("local_"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,26)); entity.setCode(support.nextNumber("logistics_provider","code"));
  support.validate("logistics_provider",entity,null,null);
  mapper.insert(entity);support.clearRemovedAfterWrite("logistics_provider",entity.getId());support.saveRelations("logistics_provider",entity.getId(),null);return entity.getId();
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean updateByBo(OzonBizLogisticsProviderBo bo) {
  support.lockAndCheck("logistics_provider",bo.getId(),bo.getRevision());
  var entity=mapper.selectById(bo.getId());
  if(entity==null) throw new ServiceException("记录不存在或已删除");
  entity.setName(bo.getName());
entity.setWarehouse(bo.getWarehouse());
entity.setWarehouseFee(bo.getWarehouseFee());
entity.setBank(bo.getBank());
entity.setSystem(bo.getSystem());
entity.setCustomerCode(bo.getCustomerCode());
  support.validate("logistics_provider",entity,bo.getId(),null);mapper.updateById(entity);support.clearRemovedAfterWrite("logistics_provider",entity.getId());
  support.saveRelations("logistics_provider",entity.getId(),null);return true;
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean deleteWithValidById(Long id,String revision) {
  support.lockAndCheck("logistics_provider",id,revision);support.beforeDelete("logistics_provider",id);return mapper.deleteById(id)>0;
 }
}
