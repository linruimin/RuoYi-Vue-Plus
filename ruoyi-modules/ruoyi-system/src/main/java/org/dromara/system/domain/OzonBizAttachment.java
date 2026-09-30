package org.dromara.system.domain;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
/** 附件基础记录，沿用来源表字段。 */
@Data @TableName("attachment")
public class OzonBizAttachment implements Serializable {
/** 记录ID。 */
@TableId(value="id",type=IdType.AUTO)
private Long id;
/** 所属业务。 */
@TableField(value="`source_table`",updateStrategy=FieldStrategy.ALWAYS)
private String sourceTable;
/** 来源记录ID。 */
@TableField(value="`feishu_record_id`",updateStrategy=FieldStrategy.ALWAYS)
private String feishuRecordId;
/** 附件字段。 */
@TableField(value="`field_name`",updateStrategy=FieldStrategy.ALWAYS)
private String fieldName;
/** 文件名。 */
@TableField(value="`file_name`",updateStrategy=FieldStrategy.ALWAYS)
private String fileName;
/** COS 对象 Key。 */
@TableField(value="`cos_key`",updateStrategy=FieldStrategy.ALWAYS)
private String cosKey;
/** COS 公开 URL。 */
@TableField(value="`cos_url`",updateStrategy=FieldStrategy.ALWAYS)
private String cosUrl;
/** 文件字节。 */
@TableField(value="`size_bytes`",updateStrategy=FieldStrategy.ALWAYS)
private Long sizeBytes;
/** MIME 类型。 */
@TableField(value="`mime_type`",updateStrategy=FieldStrategy.ALWAYS)
private String mimeType;
/** 创建时间。 */
@TableField(value="`created_at`")
private LocalDateTime createdAt;
}
