package org.dromara.system.service;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizAttachmentVo;
import org.dromara.system.domain.vo.OzonAttachmentUploadVo;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.springframework.web.multipart.MultipartFile;
/** 附件业务服务。 */
public interface IOzonBizAttachmentService {
PageResult<OzonBizAttachmentVo> queryPageList(OzonBusinessQuery query,PageQuery page);
OzonBizAttachmentVo queryById(Long id);
Long insertByBo(OzonBizAttachmentBo bo);
boolean updateByBo(OzonBizAttachmentBo bo);
boolean deleteWithValidById(Long id,String revision);
/** 上传业务图片到对象存储，只返回对象信息，登记在保存业务记录时一并完成。 */
OzonAttachmentUploadVo upload(String sourceTable,String fieldName,String fileName,MultipartFile file);
}
