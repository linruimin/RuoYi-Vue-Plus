package org.dromara.system.service;
import org.dromara.system.domain.bo.OzonBusinessQuery;
import org.dromara.system.domain.vo.OzonBizReturnsVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
/** 退货业务服务（只读，数据由 Ozon 报表同步维护）。 */
public interface IOzonBizReturnsService {
PageResult<OzonBizReturnsVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizReturnsVo queryById(Long id);
}
