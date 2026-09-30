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
import org.dromara.system.domain.OzonBizStudioReceipt;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizStudioReceiptVo;
import org.dromara.system.mapper.OzonBizStudioReceiptMapper;
import org.dromara.system.service.IOzonBizStudioReceiptService;
/** 工作室收货事务管理与关联校验。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizStudioReceiptServiceImpl implements IOzonBizStudioReceiptService {
 private final OzonBizStudioReceiptMapper mapper;
 private final OzonBusinessSupport support;
 public PageResult<OzonBizStudioReceiptVo> queryPageList(OzonBusinessQuery q,PageQuery p) {
  Page<OzonBizStudioReceiptVo> page=mapper.selectBusinessPage(new Page<>(Math.max(1,p.getPageNum()),Math.min(100,Math.max(1,p.getPageSize()))),support.<OzonBizStudioReceipt>query("studio_receipt",q,p));
  for(var row:page.getRecords()) enrich(row);
  return PageResult.build(page.getRecords(),page.getTotal());
 }
 private void enrich(OzonBizStudioReceiptVo row) { row.setRevision(support.revision("studio_receipt",row.getId()));  }
 @Transactional(rollbackFor=Exception.class)
 public OzonBizStudioReceiptVo queryById(Long id) {
  support.lockForRead("studio_receipt",id);
  var rows=mapper.selectBusinessPage(new Page<>(1,1),new QueryWrapper<OzonBizStudioReceipt>().eq("b.id",id)).getRecords();
  if(rows.isEmpty()) throw new ServiceException("记录不存在或已删除");
  var row=rows.getFirst();enrich(row);return row;
 }
 @Transactional(rollbackFor=Exception.class)
 public Long insertByBo(OzonBizStudioReceiptBo bo) {
  var entity=MapstructUtils.convert(bo,OzonBizStudioReceipt.class);entity.setId(null); entity.setFeishuRecordId("local_"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,26)); 
  support.validate("studio_receipt",entity,null,null);
  mapper.insert(entity);support.clearRemovedAfterWrite("studio_receipt",entity.getId());support.saveRelations("studio_receipt",entity.getId(),null);return entity.getId();
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean updateByBo(OzonBizStudioReceiptBo bo) {
  support.lockAndCheck("studio_receipt",bo.getId(),bo.getRevision());
  var entity=mapper.selectById(bo.getId());
  if(entity==null) throw new ServiceException("记录不存在或已删除");
  entity.setOrderNo(bo.getOrderNo());
entity.setQuantity(bo.getQuantity());
entity.setBoxCount(bo.getBoxCount());
entity.setBoxSpec(bo.getBoxSpec());
entity.setBoxWeight(bo.getBoxWeight());
entity.setReceivedAt(bo.getReceivedAt());
entity.setRemark(bo.getRemark());
  support.validate("studio_receipt",entity,bo.getId(),null);mapper.updateById(entity);support.clearRemovedAfterWrite("studio_receipt",entity.getId());
  support.saveRelations("studio_receipt",entity.getId(),null);return true;
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean deleteWithValidById(Long id,String revision) {
  support.lockAndCheck("studio_receipt",id,revision);support.beforeDelete("studio_receipt",id);return mapper.deleteById(id)>0;
 }
}
