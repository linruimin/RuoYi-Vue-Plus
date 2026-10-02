package org.dromara.system.domain.vo;
import lombok.Data;
import java.io.Serializable;
/** 图片上传结果：只返回对象信息，附件登记在保存业务记录时一并完成。 */
@Data
public class OzonAttachmentUploadVo implements Serializable {
/** 最终文件名（含扩展名）。 */
private String fileName;
/** COS 对象 Key。 */
private String cosKey;
/** COS 公开 URL。 */
private String cosUrl;
/** 文件字节。 */
private Long sizeBytes;
/** MIME 类型。 */
private String mimeType;
}
