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
import org.dromara.system.domain.OzonBizReplenishment;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizReplenishmentVo;
import org.dromara.system.mapper.OzonBizReplenishmentMapper;
import org.dromara.system.service.IOzonBizReplenishmentService;
/** 补货事务管理与关联校验。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizReplenishmentServiceImpl implements IOzonBizReplenishmentService {
 private final OzonBizReplenishmentMapper mapper;
 private final OzonBusinessSupport support;
 public PageResult<OzonBizReplenishmentVo> queryPageList(OzonBusinessQuery q,PageQuery p) {
  Page<OzonBizReplenishmentVo> page=mapper.selectBusinessPage(new Page<>(Math.max(1,p.getPageNum()),Math.min(100,Math.max(1,p.getPageSize()))),support.<OzonBizReplenishment>query("replenishment",q,p));
  for(var row:page.getRecords()) enrich(row);
  return PageResult.build(page.getRecords(),page.getTotal());
 }
 private void enrich(OzonBizReplenishmentVo row) { row.setRevision(support.revision("replenishment",row.getId()));  }
 @Transactional(rollbackFor=Exception.class)
 public OzonBizReplenishmentVo queryById(Long id) {
  support.lockForRead("replenishment",id);
  var rows=mapper.selectBusinessPage(new Page<>(1,1),new QueryWrapper<OzonBizReplenishment>().eq("b.id",id)).getRecords();
  if(rows.isEmpty()) throw new ServiceException("记录不存在或已删除");
  var row=rows.getFirst();enrich(row);return row;
 }
 @Transactional(rollbackFor=Exception.class)
 public Long insertByBo(OzonBizReplenishmentBo bo) {
  var entity=MapstructUtils.convert(bo,OzonBizReplenishment.class);entity.setId(null); entity.setFeishuRecordId("local_"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,26)); entity.setReplenishNo(support.nextNumber("replenishment","replenish_no"));
  support.validate("replenishment",entity,null,null);
  mapper.insert(entity);support.clearRemovedAfterWrite("replenishment",entity.getId());support.saveRelations("replenishment",entity.getId(),null);return entity.getId();
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean updateByBo(OzonBizReplenishmentBo bo) {
  support.lockAndCheck("replenishment",bo.getId(),bo.getRevision());
  var entity=mapper.selectById(bo.getId());
  if(entity==null) throw new ServiceException("记录不存在或已删除");
  entity.setProductId(bo.getProductId());
entity.setExpectedDate(bo.getExpectedDate());
entity.setExpectedQty(bo.getExpectedQty());
entity.setMethod(bo.getMethod());
entity.setRemark(bo.getRemark());
  support.validate("replenishment",entity,bo.getId(),null);mapper.updateById(entity);support.clearRemovedAfterWrite("replenishment",entity.getId());
  support.saveRelations("replenishment",entity.getId(),null);return true;
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean deleteWithValidById(Long id,String revision) {
  support.lockAndCheck("replenishment",id,revision);support.beforeDelete("replenishment",id);return mapper.deleteById(id)>0;
 }
}
