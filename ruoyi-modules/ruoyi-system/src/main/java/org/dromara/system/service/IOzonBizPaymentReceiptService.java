package org.dromara.system.service;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizPaymentReceiptVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
/** 回款业务服务。 */
public interface IOzonBizPaymentReceiptService {
PageResult<OzonBizPaymentReceiptVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizPaymentReceiptVo queryById(Long id);
Long insertByBo(OzonBizPaymentReceiptBo bo);
boolean updateByBo(OzonBizPaymentReceiptBo bo);
boolean deleteWithValidById(Long id,String revision);
}
