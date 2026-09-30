package org.dromara.system.service;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizShopVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
/** 店铺业务服务。 */
public interface IOzonBizShopService {
PageResult<OzonBizShopVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizShopVo queryById(Long id);
Long insertByBo(OzonBizShopBo bo);
boolean updateByBo(OzonBizShopBo bo);
boolean deleteWithValidById(Long id,String revision);
}
