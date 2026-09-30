package org.dromara.system.service;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizLogisticsProviderVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
/** 物流商资料业务服务。 */
public interface IOzonBizLogisticsProviderService {
PageResult<OzonBizLogisticsProviderVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizLogisticsProviderVo queryById(Long id);
Long insertByBo(OzonBizLogisticsProviderBo bo);
boolean updateByBo(OzonBizLogisticsProviderBo bo);
boolean deleteWithValidById(Long id,String revision);
}
