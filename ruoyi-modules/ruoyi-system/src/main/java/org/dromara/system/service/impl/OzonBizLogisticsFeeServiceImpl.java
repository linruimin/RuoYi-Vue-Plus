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
import org.dromara.system.domain.OzonBizLogisticsFee;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizLogisticsFeeVo;
import org.dromara.system.mapper.OzonBizLogisticsFeeMapper;
import org.dromara.system.service.IOzonBizLogisticsFeeService;
/** 物流费用事务管理与关联校验。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizLogisticsFeeServiceImpl implements IOzonBizLogisticsFeeService {
 private final OzonBizLogisticsFeeMapper mapper;
 private final OzonBusinessSupport support;
 public PageResult<OzonBizLogisticsFeeVo> queryPageList(OzonBusinessQuery q,PageQuery p) {
  Page<OzonBizLogisticsFeeVo> page=mapper.selectBusinessPage(new Page<>(Math.max(1,p.getPageNum()),Math.min(100,Math.max(1,p.getPageSize()))),support.<OzonBizLogisticsFee>query("logistics_fee",q,p));
  for(var row:page.getRecords()) enrich(row);
  return PageResult.build(page.getRecords(),page.getTotal());
 }
 private void enrich(OzonBizLogisticsFeeVo row) { row.setShipmentIdsLabel(support.relationLabels("logistics_fee",row.getId())); row.setRevision(support.revision("logistics_fee",row.getId())); row.setShipmentIds(support.relations("logistics_fee",row.getId())); }
 @Transactional(rollbackFor=Exception.class)
 public OzonBizLogisticsFeeVo queryById(Long id) {
  support.lockForRead("logistics_fee",id);
  var rows=mapper.selectBusinessPage(new Page<>(1,1),new QueryWrapper<OzonBizLogisticsFee>().eq("b.id",id)).getRecords();
  if(rows.isEmpty()) throw new ServiceException("记录不存在或已删除");
  var row=rows.getFirst();enrich(row);return row;
 }
 @Transactional(rollbackFor=Exception.class)
 public Long insertByBo(OzonBizLogisticsFeeBo bo) {
  var entity=MapstructUtils.convert(bo,OzonBizLogisticsFee.class);entity.setId(null); entity.setFeishuRecordId("local_"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,26)); entity.setFeeNo(support.nextNumber("logistics_fee","fee_no"));
  support.validate("logistics_fee",entity,null,bo.getShipmentIds());
  mapper.insert(entity);support.clearRemovedAfterWrite("logistics_fee",entity.getId());support.saveRelations("logistics_fee",entity.getId(),bo.getShipmentIds());return entity.getId();
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean updateByBo(OzonBizLogisticsFeeBo bo) {
  support.lockAndCheck("logistics_fee",bo.getId(),bo.getRevision());
  var entity=mapper.selectById(bo.getId());
  if(entity==null) throw new ServiceException("记录不存在或已删除");
  entity.setFeeType(bo.getFeeType());
entity.setAmount(bo.getAmount());
entity.setPaidFlag(bo.getPaidFlag());
entity.setFeeDate(bo.getFeeDate());
entity.setShopId(bo.getShopId());
entity.setRemark(bo.getRemark());
  support.validate("logistics_fee",entity,bo.getId(),bo.getShipmentIds());mapper.updateById(entity);support.clearRemovedAfterWrite("logistics_fee",entity.getId());
  support.saveRelations("logistics_fee",entity.getId(),bo.getShipmentIds());return true;
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean deleteWithValidById(Long id,String revision) {
  support.lockAndCheck("logistics_fee",id,revision);support.beforeDelete("logistics_fee",id);return mapper.deleteById(id)>0;
 }
}
