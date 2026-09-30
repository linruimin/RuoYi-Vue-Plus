package org.dromara.system.service;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizShipmentVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
/** 出货业务服务。 */
public interface IOzonBizShipmentService {
PageResult<OzonBizShipmentVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizShipmentVo queryById(Long id);
Long insertByBo(OzonBizShipmentBo bo);
boolean updateByBo(OzonBizShipmentBo bo);
boolean deleteWithValidById(Long id,String revision);
}
