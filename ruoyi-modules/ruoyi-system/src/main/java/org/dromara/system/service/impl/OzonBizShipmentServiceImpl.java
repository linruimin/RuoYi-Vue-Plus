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
import org.dromara.system.domain.OzonBizShipment;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizShipmentVo;
import org.dromara.system.mapper.OzonBizShipmentMapper;
import org.dromara.system.service.IOzonBizShipmentService;
/** 出货事务管理与关联校验。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizShipmentServiceImpl implements IOzonBizShipmentService {
 private final OzonBizShipmentMapper mapper;
 private final OzonBusinessSupport support;
 public PageResult<OzonBizShipmentVo> queryPageList(OzonBusinessQuery q,PageQuery p) {
  Page<OzonBizShipmentVo> page=mapper.selectBusinessPage(new Page<>(Math.max(1,p.getPageNum()),Math.min(100,Math.max(1,p.getPageSize()))),support.<OzonBizShipment>query("shipment",q,p));
  for(var row:page.getRecords()) enrich(row);
  return PageResult.build(page.getRecords(),page.getTotal());
 }
 private void enrich(OzonBizShipmentVo row) { row.setLogisticsFeeIdsLabel(support.relationLabels("shipment",row.getId())); row.setRevision(support.revision("shipment",row.getId())); row.setLogisticsFeeIds(support.relations("shipment",row.getId())); }
 @Transactional(rollbackFor=Exception.class)
 public OzonBizShipmentVo queryById(Long id) {
  support.lockForRead("shipment",id);
  var rows=mapper.selectBusinessPage(new Page<>(1,1),new QueryWrapper<OzonBizShipment>().eq("b.id",id)).getRecords();
  if(rows.isEmpty()) throw new ServiceException("记录不存在或已删除");
  var row=rows.getFirst();enrich(row);return row;
 }
 @Transactional(rollbackFor=Exception.class)
 public Long insertByBo(OzonBizShipmentBo bo) {
  var entity=MapstructUtils.convert(bo,OzonBizShipment.class);entity.setId(null); entity.setFeishuRecordId("local_"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,26)); entity.setShipmentNo(support.nextNumber("shipment","shipment_no"));
  support.validate("shipment",entity,null,bo.getLogisticsFeeIds());
  mapper.insert(entity);support.clearRemovedAfterWrite("shipment",entity.getId());support.saveRelations("shipment",entity.getId(),bo.getLogisticsFeeIds());return entity.getId();
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean updateByBo(OzonBizShipmentBo bo) {
  support.lockAndCheck("shipment",bo.getId(),bo.getRevision());
  var entity=mapper.selectById(bo.getId());
  if(entity==null) throw new ServiceException("记录不存在或已删除");
  entity.setPurchaseId(bo.getPurchaseId());
entity.setShopId(bo.getShopId());
entity.setLogisticsMethod(bo.getLogisticsMethod());
entity.setGoodsStatus(bo.getGoodsStatus());
entity.setBoxSpec(bo.getBoxSpec());
entity.setBoxes(bo.getBoxes());
entity.setPerBoxQty(bo.getPerBoxQty());
entity.setBoxWeight(bo.getBoxWeight());
entity.setWarehouseInNo(bo.getWarehouseInNo());
entity.setShippingMark(bo.getShippingMark());
entity.setPerBoxPackingFee(bo.getPerBoxPackingFee());
entity.setPerBoxShopBuyFee(bo.getPerBoxShopBuyFee());
entity.setPerBoxPickupFee(bo.getPerBoxPickupFee());
entity.setCrossRateUsdPerKg(bo.getCrossRateUsdPerKg());
entity.setShippedAt(bo.getShippedAt());
entity.setWarehouseInAt(bo.getWarehouseInAt());
  support.validate("shipment",entity,bo.getId(),bo.getLogisticsFeeIds());mapper.updateById(entity);support.clearRemovedAfterWrite("shipment",entity.getId());
  support.saveRelations("shipment",entity.getId(),bo.getLogisticsFeeIds());return true;
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean deleteWithValidById(Long id,String revision) {
  support.lockAndCheck("shipment",id,revision);support.beforeDelete("shipment",id);return mapper.deleteById(id)>0;
 }
}
