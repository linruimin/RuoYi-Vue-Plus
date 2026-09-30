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
import org.dromara.system.domain.OzonBizPurchaseOrder;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizPurchaseOrderVo;
import org.dromara.system.mapper.OzonBizPurchaseOrderMapper;
import org.dromara.system.service.IOzonBizPurchaseOrderService;
/** 进货事务管理与关联校验。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizPurchaseOrderServiceImpl implements IOzonBizPurchaseOrderService {
 private final OzonBizPurchaseOrderMapper mapper;
 private final OzonBusinessSupport support;
 public PageResult<OzonBizPurchaseOrderVo> queryPageList(OzonBusinessQuery q,PageQuery p) {
  Page<OzonBizPurchaseOrderVo> page=mapper.selectBusinessPage(new Page<>(Math.max(1,p.getPageNum()),Math.min(100,Math.max(1,p.getPageSize()))),support.<OzonBizPurchaseOrder>query("purchase_order",q,p));
  for(var row:page.getRecords()) enrich(row);
  return PageResult.build(page.getRecords(),page.getTotal());
 }
 private void enrich(OzonBizPurchaseOrderVo row) { row.setRevision(support.revision("purchase_order",row.getId()));  }
 @Transactional(rollbackFor=Exception.class)
 public OzonBizPurchaseOrderVo queryById(Long id) {
  support.lockForRead("purchase_order",id);
  var rows=mapper.selectBusinessPage(new Page<>(1,1),new QueryWrapper<OzonBizPurchaseOrder>().eq("b.id",id)).getRecords();
  if(rows.isEmpty()) throw new ServiceException("记录不存在或已删除");
  var row=rows.getFirst();enrich(row);return row;
 }
 @Transactional(rollbackFor=Exception.class)
 public Long insertByBo(OzonBizPurchaseOrderBo bo) {
  var entity=MapstructUtils.convert(bo,OzonBizPurchaseOrder.class);entity.setId(null); entity.setFeishuRecordId("local_"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,26)); 
  support.validate("purchase_order",entity,null,null);
  mapper.insert(entity);support.clearRemovedAfterWrite("purchase_order",entity.getId());support.saveRelations("purchase_order",entity.getId(),null);return entity.getId();
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean updateByBo(OzonBizPurchaseOrderBo bo) {
  support.lockAndCheck("purchase_order",bo.getId(),bo.getRevision());
  var entity=mapper.selectById(bo.getId());
  if(entity==null) throw new ServiceException("记录不存在或已删除");
  entity.setOrderNo(bo.getOrderNo());
entity.setProductId(bo.getProductId());
entity.setShopId(bo.getShopId());
entity.setActualPaid(bo.getActualPaid());
entity.setQuantity(bo.getQuantity());
entity.setPayStatus(bo.getPayStatus());
entity.setChannel(bo.getChannel());
entity.setPaidAt(bo.getPaidAt());
entity.setRemark(bo.getRemark());
  support.validate("purchase_order",entity,bo.getId(),null);mapper.updateById(entity);support.clearRemovedAfterWrite("purchase_order",entity.getId());
  support.saveRelations("purchase_order",entity.getId(),null);return true;
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean deleteWithValidById(Long id,String revision) {
  support.lockAndCheck("purchase_order",id,revision);support.beforeDelete("purchase_order",id);return mapper.deleteById(id)>0;
 }
}
