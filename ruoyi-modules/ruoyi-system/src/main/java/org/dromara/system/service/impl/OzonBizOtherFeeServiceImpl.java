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
import org.dromara.system.domain.OzonBizOtherFee;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizOtherFeeVo;
import org.dromara.system.mapper.OzonBizOtherFeeMapper;
import org.dromara.system.service.IOzonBizOtherFeeService;
/** 其他费用事务管理与关联校验。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizOtherFeeServiceImpl implements IOzonBizOtherFeeService {
 private final OzonBizOtherFeeMapper mapper;
 private final OzonBusinessSupport support;
 public PageResult<OzonBizOtherFeeVo> queryPageList(OzonBusinessQuery q,PageQuery p) {
  Page<OzonBizOtherFeeVo> page=mapper.selectBusinessPage(new Page<>(Math.max(1,p.getPageNum()),Math.min(100,Math.max(1,p.getPageSize()))),support.<OzonBizOtherFee>query("other_fee",q,p));
  for(var row:page.getRecords()) enrich(row);
  return PageResult.build(page.getRecords(),page.getTotal());
 }
 private void enrich(OzonBizOtherFeeVo row) { row.setRevision(support.revision("other_fee",row.getId()));  }
 @Transactional(rollbackFor=Exception.class)
 public OzonBizOtherFeeVo queryById(Long id) {
  support.lockForRead("other_fee",id);
  var rows=mapper.selectBusinessPage(new Page<>(1,1),new QueryWrapper<OzonBizOtherFee>().eq("b.id",id)).getRecords();
  if(rows.isEmpty()) throw new ServiceException("记录不存在或已删除");
  var row=rows.getFirst();enrich(row);return row;
 }
 @Transactional(rollbackFor=Exception.class)
 public Long insertByBo(OzonBizOtherFeeBo bo) {
  var entity=MapstructUtils.convert(bo,OzonBizOtherFee.class);entity.setId(null); entity.setFeishuRecordId("local_"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,26)); entity.setFeeNo(support.nextNumber("other_fee","fee_no"));
  support.validate("other_fee",entity,null,null);
  mapper.insert(entity);support.clearRemovedAfterWrite("other_fee",entity.getId());support.saveRelations("other_fee",entity.getId(),null);support.saveAttachments("other_fee",entity.getFeishuRecordId(),"附件",bo.getAttachments());return entity.getId();
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean updateByBo(OzonBizOtherFeeBo bo) {
  support.lockAndCheck("other_fee",bo.getId(),bo.getRevision());
  var entity=mapper.selectById(bo.getId());
  if(entity==null) throw new ServiceException("记录不存在或已删除");
  entity.setFeeType(bo.getFeeType());
entity.setAmount(bo.getAmount());
entity.setFeeDate(bo.getFeeDate());
entity.setShopId(bo.getShopId());
entity.setRemark(bo.getRemark());
  support.validate("other_fee",entity,bo.getId(),null);mapper.updateById(entity);support.clearRemovedAfterWrite("other_fee",entity.getId());
  support.saveRelations("other_fee",entity.getId(),null);support.saveAttachments("other_fee",entity.getFeishuRecordId(),"附件",bo.getAttachments());return true;
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean deleteWithValidById(Long id,String revision) {
  support.lockAndCheck("other_fee",id,revision);support.beforeDelete("other_fee",id);return mapper.deleteById(id)>0;
 }
}
