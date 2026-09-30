package org.dromara.system.domain;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
/** 店铺基础记录，沿用来源表字段。 */
@Data @TableName("shop")
public class OzonBizShop implements Serializable {
/** 记录ID。 */
@TableId(value="id",type=IdType.AUTO)
private Long id;
/** 来源记录ID。 */
@TableField(value="`feishu_record_id`")
private String feishuRecordId;
/** 店铺名称。 */
@TableField(value="`name`",updateStrategy=FieldStrategy.ALWAYS)
private String name;
/** 俄文名称。 */
@TableField(value="`name_ru`",updateStrategy=FieldStrategy.ALWAYS)
private String nameRu;
/** 创建时间。 */
@TableField(value="`created_at`")
private LocalDateTime createdAt;
/** 更新时间。 */
@TableField(value="`updated_at`")
private LocalDateTime updatedAt;
}
