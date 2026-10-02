package org.dromara.system.domain.bo;
import lombok.Data;
import java.io.Serializable;
/** 随业务记录一起提交的附件引用，只作传输载体，不落到业务表。 */
@Data
public class OzonAttachmentRef implements Serializable {
/** 已登记的附件ID；为空表示本次新上传、尚未登记。 */
private Long id;
/** 文件名（含扩展名）。 */
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
