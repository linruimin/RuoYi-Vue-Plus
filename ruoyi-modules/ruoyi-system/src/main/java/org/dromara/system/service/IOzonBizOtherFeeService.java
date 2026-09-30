package org.dromara.system.service;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizOtherFeeVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
/** 其他费用业务服务。 */
public interface IOzonBizOtherFeeService {
PageResult<OzonBizOtherFeeVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizOtherFeeVo queryById(Long id);
Long insertByBo(OzonBizOtherFeeBo bo);
boolean updateByBo(OzonBizOtherFeeBo bo);
boolean deleteWithValidById(Long id,String revision);
}
