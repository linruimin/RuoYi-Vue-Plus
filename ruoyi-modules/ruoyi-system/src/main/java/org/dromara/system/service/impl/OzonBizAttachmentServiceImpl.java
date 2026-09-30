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
import org.dromara.system.domain.OzonBizAttachment;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizAttachmentVo;
import org.dromara.system.mapper.OzonBizAttachmentMapper;
import org.dromara.system.service.IOzonBizAttachmentService;
/** 附件事务管理与关联校验。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizAttachmentServiceImpl implements IOzonBizAttachmentService {
 private final OzonBizAttachmentMapper mapper;
 private final OzonBusinessSupport support;
 public PageResult<OzonBizAttachmentVo> queryPageList(OzonBusinessQuery q,PageQuery p) {
  Page<OzonBizAttachmentVo> page=mapper.selectBusinessPage(new Page<>(Math.max(1,p.getPageNum()),Math.min(100,Math.max(1,p.getPageSize()))),support.<OzonBizAttachment>query("attachment",q,p));
  for(var row:page.getRecords()) enrich(row);
  return PageResult.build(page.getRecords(),page.getTotal());
 }
 private void enrich(OzonBizAttachmentVo row) { row.setRevision(support.revision("attachment",row.getId()));  }
 @Transactional(rollbackFor=Exception.class)
 public OzonBizAttachmentVo queryById(Long id) {
  support.lockForRead("attachment",id);
  var rows=mapper.selectBusinessPage(new Page<>(1,1),new QueryWrapper<OzonBizAttachment>().eq("b.id",id)).getRecords();
  if(rows.isEmpty()) throw new ServiceException("记录不存在或已删除");
  var row=rows.getFirst();enrich(row);return row;
 }
 @Transactional(rollbackFor=Exception.class)
 public Long insertByBo(OzonBizAttachmentBo bo) {
  var entity=MapstructUtils.convert(bo,OzonBizAttachment.class);entity.setId(null);  
  support.validate("attachment",entity,null,null);
  mapper.insert(entity);support.clearRemovedAfterWrite("attachment",entity.getId());support.saveRelations("attachment",entity.getId(),null);return entity.getId();
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean updateByBo(OzonBizAttachmentBo bo) {
  support.lockAndCheck("attachment",bo.getId(),bo.getRevision());
  var entity=mapper.selectById(bo.getId());
  if(entity==null) throw new ServiceException("记录不存在或已删除");
  entity.setSourceTable(bo.getSourceTable());
entity.setFeishuRecordId(bo.getFeishuRecordId());
entity.setFieldName(bo.getFieldName());
entity.setFileName(bo.getFileName());
entity.setCosKey(bo.getCosKey());
entity.setCosUrl(bo.getCosUrl());
entity.setSizeBytes(bo.getSizeBytes());
entity.setMimeType(bo.getMimeType());
  support.validate("attachment",entity,bo.getId(),null);mapper.updateById(entity);support.clearRemovedAfterWrite("attachment",entity.getId());
  support.saveRelations("attachment",entity.getId(),null);return true;
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean deleteWithValidById(Long id,String revision) {
  support.lockAndCheck("attachment",id,revision);support.beforeDelete("attachment",id);return mapper.deleteById(id)>0;
 }
}
