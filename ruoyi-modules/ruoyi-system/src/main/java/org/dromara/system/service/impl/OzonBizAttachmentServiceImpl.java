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
import org.dromara.system.domain.vo.OzonAttachmentUploadVo;
import org.dromara.system.mapper.OzonBizAttachmentMapper;
import org.dromara.system.service.IOzonBizAttachmentService;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.oss.model.PutObjectResult;
import org.springframework.web.multipart.MultipartFile;
import cn.hutool.core.util.IdUtil;
import java.io.IOException;
import java.util.*;
/** 附件事务管理与关联校验。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizAttachmentServiceImpl implements IOzonBizAttachmentService {
 /** 单张图片的大小上限，与 spring.servlet.multipart.max-file-size 保持一致。 */
 private static final long MAX_BYTES=10L*1024*1024;
 /** 允许的图片类型与其扩展名。 */
 private static final Map<String,String> ALLOWED=Map.of("image/png","png","image/jpeg","jpg","image/webp","webp");
 /** 不支持作为附件归属的表。 */
 private static final Set<String> NO_ATTACHMENT=Set.of("attachment","returns","shop");
 private final OzonBizAttachmentMapper mapper;
 private final OzonBusinessSupport support;
 private final OzonCosClient cosClient;
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
 /** 选图即传对象存储，此时业务记录可能还不存在，因此只上传、不登记。 */
 public OzonAttachmentUploadVo upload(String sourceTable,String fieldName,String fileName,MultipartFile file) {
  if(file==null||file.isEmpty())throw new ServiceException("请选择要上传的图片");
  support.requireTable(sourceTable);
  if(NO_ATTACHMENT.contains(sourceTable))throw new ServiceException("该业务不支持上传图片");
  if(StringUtils.isBlank(fieldName)||fieldName.length()>64)throw new ServiceException("附件字段不正确");
  if(file.getSize()>MAX_BYTES)throw new ServiceException("单张图片不能超过 10MB");
  String mime=StringUtils.trimToEmpty(file.getContentType()).toLowerCase().split(";")[0].trim();
  String extension=ALLOWED.get(mime);
  if(extension==null)throw new ServiceException("只支持 png、jpg、webp 格式的图片");
  String name=resolveName(fileName,extension);
  String key=support.attachmentFolder(sourceTable)+"/"+name;
  byte[] data;
  try{
   data=file.getBytes();
  }catch(IOException ex){
   throw new ServiceException("图片读取失败，请重新选择后再试");
  }
  PutObjectResult result=cosClient.upload(key,data,mime);
  var vo=new OzonAttachmentUploadVo();
  vo.setFileName(name);vo.setCosKey(key);vo.setCosUrl(result.url());
  vo.setSizeBytes(file.getSize());vo.setMimeType(mime);
  return vo;
 }
 /** 统一文件名：沿用「编号.扩展名」的对账习惯，剔除路径分隔符，扩展名以服务端判定的类型为准。 */
 private static String resolveName(String fileName,String extension) {
  String name=StringUtils.trimToEmpty(fileName).replaceAll("[\\\\/:*?\"<>|\\s]","_");
  while(name.startsWith("."))name=name.substring(1);
  if(name.length()>128)name=name.substring(0,128);
  if(name.isEmpty())name=IdUtil.fastSimpleUUID();
  String suffix="."+extension;
  if(!name.toLowerCase().endsWith(suffix)){
   int dot=name.lastIndexOf('.');
   name=(dot>0?name.substring(0,dot):name)+suffix;
  }
  return name;
 }
}
