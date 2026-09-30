package org.dromara.system.service;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizPurchaseOrderVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
/** 进货业务服务。 */
public interface IOzonBizPurchaseOrderService {
PageResult<OzonBizPurchaseOrderVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizPurchaseOrderVo queryById(Long id);
Long insertByBo(OzonBizPurchaseOrderBo bo);
boolean updateByBo(OzonBizPurchaseOrderBo bo);
boolean deleteWithValidById(Long id,String revision);
}
