package org.dromara.system.domain.vo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizAttachment;
/** 附件展示对象。 */
@Data @AutoMapper(target=OzonBizAttachment.class)
public class OzonBizAttachmentVo implements Serializable {
/** 记录ID。 */
private Long id;
/** 所属业务。 */
private String sourceTable;
/** 来源记录ID。 */
private String feishuRecordId;
/** 附件字段。 */
private String fieldName;
/** 文件名。 */
private String fileName;
/** COS 对象 Key。 */
private String cosKey;
/** COS 公开 URL。 */
private String cosUrl;
/** 文件字节。 */
private Long sizeBytes;
/** MIME 类型。 */
private String mimeType;
/** 创建时间。 */
private LocalDateTime createdAt;
private String revision;
/** 附件表中的公开文件信息，仅用于图片预览。 */
private String attachmentJson;
}
