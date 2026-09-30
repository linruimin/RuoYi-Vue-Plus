package org.dromara.system.service;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizLogisticsFeeVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
/** 物流费用业务服务。 */
public interface IOzonBizLogisticsFeeService {
PageResult<OzonBizLogisticsFeeVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizLogisticsFeeVo queryById(Long id);
Long insertByBo(OzonBizLogisticsFeeBo bo);
boolean updateByBo(OzonBizLogisticsFeeBo bo);
boolean deleteWithValidById(Long id,String revision);
}
