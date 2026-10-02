package org.dromara.system.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.system.domain.OzonBizReturns;
import org.dromara.system.domain.bo.OzonBusinessQuery;
import org.dromara.system.domain.vo.OzonBizReturnsVo;
import org.dromara.system.mapper.OzonBizReturnsMapper;
import org.dromara.system.service.IOzonBizReturnsService;
/** 退货只读查询：数据由 scripts/ozon_returns_sync.py 定时同步，页面不提供增删改。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBizReturnsServiceImpl implements IOzonBizReturnsService {
 private final OzonBizReturnsMapper mapper;
 private final OzonBusinessSupport support;
 public PageResult<OzonBizReturnsVo> queryPageList(OzonBusinessQuery q,PageQuery p) {
  Page<OzonBizReturnsVo> page=mapper.selectBusinessPage(new Page<>(Math.max(1,p.getPageNum()),Math.min(100,Math.max(1,p.getPageSize()))),support.<OzonBizReturns>query("returns",q,p));
  return PageResult.build(page.getRecords(),page.getTotal());
 }
 public OzonBizReturnsVo queryById(Long id) {
  var rows=mapper.selectBusinessPage(new Page<>(1,1),new QueryWrapper<OzonBizReturns>().eq("b.id",id)).getRecords();
  if(rows.isEmpty())throw new ServiceException("记录不存在或已删除");
  return rows.getFirst();
 }
}
