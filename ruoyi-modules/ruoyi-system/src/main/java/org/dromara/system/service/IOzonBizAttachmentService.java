package org.dromara.system.service;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizAttachmentVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
/** 附件业务服务。 */
public interface IOzonBizAttachmentService {
PageResult<OzonBizAttachmentVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizAttachmentVo queryById(Long id);
Long insertByBo(OzonBizAttachmentBo bo);
boolean updateByBo(OzonBizAttachmentBo bo);
boolean deleteWithValidById(Long id,String revision);
}
