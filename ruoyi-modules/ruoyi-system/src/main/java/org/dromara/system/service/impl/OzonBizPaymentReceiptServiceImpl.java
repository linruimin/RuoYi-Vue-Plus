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
import org.dromara.system.domain.OzonBizPaymentReceipt;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizPaymentReceiptVo;
import org.dromara.system.mapper.OzonBizPaymentReceiptMapper;
import org.dromara.system.service.IOzonBizPaymentReceiptService;
/** 回款事务管理与关联校验。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizPaymentReceiptServiceImpl implements IOzonBizPaymentReceiptService {
 private final OzonBizPaymentReceiptMapper mapper;
 private final OzonBusinessSupport support;
 public PageResult<OzonBizPaymentReceiptVo> queryPageList(OzonBusinessQuery q,PageQuery p) {
  Page<OzonBizPaymentReceiptVo> page=mapper.selectBusinessPage(new Page<>(Math.max(1,p.getPageNum()),Math.min(100,Math.max(1,p.getPageSize()))),support.<OzonBizPaymentReceipt>query("payment_receipt",q,p));
  for(var row:page.getRecords()) enrich(row);
  return PageResult.build(page.getRecords(),page.getTotal());
 }
 private void enrich(OzonBizPaymentReceiptVo row) { row.setRevision(support.revision("payment_receipt",row.getId()));  }
 @Transactional(rollbackFor=Exception.class)
 public OzonBizPaymentReceiptVo queryById(Long id) {
  support.lockForRead("payment_receipt",id);
  var rows=mapper.selectBusinessPage(new Page<>(1,1),new QueryWrapper<OzonBizPaymentReceipt>().eq("b.id",id)).getRecords();
  if(rows.isEmpty()) throw new ServiceException("记录不存在或已删除");
  var row=rows.getFirst();enrich(row);return row;
 }
 @Transactional(rollbackFor=Exception.class)
 public Long insertByBo(OzonBizPaymentReceiptBo bo) {
  var entity=MapstructUtils.convert(bo,OzonBizPaymentReceipt.class);entity.setId(null); entity.setFeishuRecordId("local_"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,26)); entity.setCode(support.nextNumber("payment_receipt","code"));
  support.validate("payment_receipt",entity,null,null);
  mapper.insert(entity);support.clearRemovedAfterWrite("payment_receipt",entity.getId());support.saveRelations("payment_receipt",entity.getId(),null);return entity.getId();
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean updateByBo(OzonBizPaymentReceiptBo bo) {
  support.lockAndCheck("payment_receipt",bo.getId(),bo.getRevision());
  var entity=mapper.selectById(bo.getId());
  if(entity==null) throw new ServiceException("记录不存在或已删除");
  entity.setPaidAt(bo.getPaidAt());
entity.setAmount(bo.getAmount());
entity.setExchangeRate(bo.getExchangeRate());
entity.setShopId(bo.getShopId());
entity.setRemark(bo.getRemark());
  support.validate("payment_receipt",entity,bo.getId(),null);mapper.updateById(entity);support.clearRemovedAfterWrite("payment_receipt",entity.getId());
  support.saveRelations("payment_receipt",entity.getId(),null);return true;
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean deleteWithValidById(Long id,String revision) {
  support.lockAndCheck("payment_receipt",id,revision);support.beforeDelete("payment_receipt",id);return mapper.deleteById(id)>0;
 }
}
