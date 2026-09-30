package org.dromara.system.service;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizStudioReceiptVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
/** 工作室收货业务服务。 */
public interface IOzonBizStudioReceiptService {
PageResult<OzonBizStudioReceiptVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizStudioReceiptVo queryById(Long id);
Long insertByBo(OzonBizStudioReceiptBo bo);
boolean updateByBo(OzonBizStudioReceiptBo bo);
boolean deleteWithValidById(Long id,String revision);
}
