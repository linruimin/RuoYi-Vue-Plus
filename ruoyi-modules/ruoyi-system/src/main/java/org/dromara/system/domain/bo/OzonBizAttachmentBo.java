package org.dromara.system.domain.bo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.dromara.common.core.validate.*;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizAttachment;
/** 附件写入参数。 */
@Data @AutoMapper(target=OzonBizAttachment.class,reverseConvertGenerate=false)
public class OzonBizAttachmentBo implements Serializable {
@NotNull(groups=EditGroup.class) private Long id;
/** 所属业务。 */
@NotBlank(message="所属业务不能为空",groups={AddGroup.class,EditGroup.class})
@Size(max=64,groups={AddGroup.class,EditGroup.class})
private String sourceTable;
/** 来源记录ID。 */
@NotBlank(message="来源记录ID不能为空",groups={AddGroup.class,EditGroup.class})
@Size(max=32,groups={AddGroup.class,EditGroup.class})
private String feishuRecordId;
/** 附件字段。 */
@NotBlank(message="附件字段不能为空",groups={AddGroup.class,EditGroup.class})
@Size(max=64,groups={AddGroup.class,EditGroup.class})
private String fieldName;
/** 文件名。 */
@NotBlank(message="文件名不能为空",groups={AddGroup.class,EditGroup.class})
@Size(max=255,groups={AddGroup.class,EditGroup.class})
private String fileName;
/** COS 对象 Key。 */
@Size(max=512,groups={AddGroup.class,EditGroup.class})
private String cosKey;
/** COS 公开 URL。 */
@Size(max=768,groups={AddGroup.class,EditGroup.class})
private String cosUrl;
/** 文件字节。 */
private Long sizeBytes;
/** MIME 类型。 */
@Size(max=128,groups={AddGroup.class,EditGroup.class})
private String mimeType;
/** 版本指纹，防止并发覆盖。 */
@NotBlank(groups=EditGroup.class) private String revision;
}
