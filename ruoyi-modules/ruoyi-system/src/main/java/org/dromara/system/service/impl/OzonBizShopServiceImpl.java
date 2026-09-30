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
import org.dromara.system.domain.OzonBizShop;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizShopVo;
import org.dromara.system.mapper.OzonBizShopMapper;
import org.dromara.system.service.IOzonBizShopService;
/** 店铺事务管理与关联校验。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizShopServiceImpl implements IOzonBizShopService {
 private final OzonBizShopMapper mapper;
 private final OzonBusinessSupport support;
 public PageResult<OzonBizShopVo> queryPageList(OzonBusinessQuery q,PageQuery p) {
  Page<OzonBizShopVo> page=mapper.selectBusinessPage(new Page<>(Math.max(1,p.getPageNum()),Math.min(100,Math.max(1,p.getPageSize()))),support.<OzonBizShop>query("shop",q,p));
  for(var row:page.getRecords()) enrich(row);
  return PageResult.build(page.getRecords(),page.getTotal());
 }
 private void enrich(OzonBizShopVo row) { row.setRevision(support.revision("shop",row.getId()));  }
 @Transactional(rollbackFor=Exception.class)
 public OzonBizShopVo queryById(Long id) {
  support.lockForRead("shop",id);
  var rows=mapper.selectBusinessPage(new Page<>(1,1),new QueryWrapper<OzonBizShop>().eq("b.id",id)).getRecords();
  if(rows.isEmpty()) throw new ServiceException("记录不存在或已删除");
  var row=rows.getFirst();enrich(row);return row;
 }
 @Transactional(rollbackFor=Exception.class)
 public Long insertByBo(OzonBizShopBo bo) {
  var entity=MapstructUtils.convert(bo,OzonBizShop.class);entity.setId(null); entity.setFeishuRecordId("local_"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,26)); 
  support.validate("shop",entity,null,null);
  mapper.insert(entity);support.clearRemovedAfterWrite("shop",entity.getId());support.saveRelations("shop",entity.getId(),null);return entity.getId();
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean updateByBo(OzonBizShopBo bo) {
  support.lockAndCheck("shop",bo.getId(),bo.getRevision());
  var entity=mapper.selectById(bo.getId());
  if(entity==null) throw new ServiceException("记录不存在或已删除");
  entity.setName(bo.getName());
entity.setNameRu(bo.getNameRu());
  support.validate("shop",entity,bo.getId(),null);mapper.updateById(entity);support.clearRemovedAfterWrite("shop",entity.getId());
  support.saveRelations("shop",entity.getId(),null);return true;
 }
 @Transactional(rollbackFor=Exception.class)
 public boolean deleteWithValidById(Long id,String revision) {
  support.lockAndCheck("shop",id,revision);support.beforeDelete("shop",id);return mapper.deleteById(id)>0;
 }
}
