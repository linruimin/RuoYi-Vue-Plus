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
import org.dromara.system.domain.OzonBizProduct;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizProductVo;
import org.dromara.system.mapper.OzonBizProductMapper;
import org.dromara.system.service.IOzonBizProductService;
/** 产品事务管理与关联校验。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizProductServiceImpl implements IOzonBizProductService {
 private final OzonBizProductMapper mapper;
 private final OzonBusinessSupport support;
 public PageResult<OzonBizProductVo> queryPageList(OzonBusinessQuery q,PageQuery p) {
  Page<OzonBizProductVo> page=mapper.selectBusinessPage(new Page<>(Math.max(1,p.getPageNum()),Math.min(100,Math.max(1,p.getPageSize()))),support.<OzonBizProduct>query("product",q,p));
  for(var row:page.getRecords()) enrich(row);
  return PageResult.build(page.getRecords(),page.getTotal());
 }
 private void enrich(OzonBizProductVo row) { row.setRevision(support.revision("product",row.getId()));  }
 @Transactional(rollbackFor=Exception.class)
 public OzonBizProductVo queryById(Long id) {
  support.lockForRead("product",id);
  var rows=mapper.selectBusinessPage(new Page<>(1,1),new QueryWrapper<OzonBizProduct>().eq("b.id",id)).getRecords();
  if(rows.isEmpty()) throw new ServiceException("记录不存在或已删除");
  var row=rows.getFirst();enrich(row);return row;
 }
 @Transactional(rollbackFor=Exception.class)
 public Long insertByBo(OzonBizProductBo bo) {
  var entity=MapstructUtils.convert(bo,OzonBizProduct.class);entity.setId(null); entity.setFeishuRecordId("local_"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,26)); entity.setProductNo(support.nextNumber("product","product_no"));
  support.validate("product",entity,null,null);
  mapper.insert(entity);support.clearRemovedAfterWrite("product",entity.getId());support.saveRelations("product",entity.getId(),null);support.saveAttachments("product",entity.getFeishuRecordId(),"货品图片",bo.getAttachments());return entity.getId();
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean updateByBo(OzonBizProductBo bo) {
  support.lockAndCheck("product",bo.getId(),bo.getRevision());
  var entity=mapper.selectById(bo.getId());
  if(entity==null) throw new ServiceException("记录不存在或已删除");
  entity.setSku(bo.getSku());
entity.setName(bo.getName());
entity.setArticleNo(bo.getArticleNo());
entity.setShopId(bo.getShopId());
entity.setSellerCompany(bo.getSellerCompany());
entity.setUnitPerPack(bo.getUnitPerPack());
entity.setBackendPrice(bo.getBackendPrice());
entity.setBuyerPay(bo.getBuyerPay());
entity.setReceivedPrice(bo.getReceivedPrice());
entity.setGreenPrice(bo.getGreenPrice());
entity.setGrayPrice(bo.getGrayPrice());
entity.setStrikePrice(bo.getStrikePrice());
entity.setParentId(bo.getParentId());
entity.setRemark(bo.getRemark());
  support.validate("product",entity,bo.getId(),null);mapper.updateById(entity);support.clearRemovedAfterWrite("product",entity.getId());
  support.saveRelations("product",entity.getId(),null);support.saveAttachments("product",entity.getFeishuRecordId(),"货品图片",bo.getAttachments());return true;
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean deleteWithValidById(Long id,String revision) {
  support.lockAndCheck("product",id,revision);support.beforeDelete("product",id);return mapper.deleteById(id)>0;
 }
}
