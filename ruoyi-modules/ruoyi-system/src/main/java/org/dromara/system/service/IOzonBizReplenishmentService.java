package org.dromara.system.service;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizReplenishmentVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
/** 补货业务服务。 */
public interface IOzonBizReplenishmentService {
PageResult<OzonBizReplenishmentVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizReplenishmentVo queryById(Long id);
Long insertByBo(OzonBizReplenishmentBo bo);
boolean updateByBo(OzonBizReplenishmentBo bo);
boolean deleteWithValidById(Long id,String revision);
}
