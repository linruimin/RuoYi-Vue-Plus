package org.dromara.system.service;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizProductVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
/** 产品业务服务。 */
public interface IOzonBizProductService {
PageResult<OzonBizProductVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizProductVo queryById(Long id);
Long insertByBo(OzonBizProductBo bo);
boolean updateByBo(OzonBizProductBo bo);
boolean deleteWithValidById(Long id,String revision);
}
