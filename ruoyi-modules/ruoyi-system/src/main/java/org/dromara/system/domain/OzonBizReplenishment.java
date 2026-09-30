package org.dromara.system.domain;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
/** 补货基础记录，沿用来源表字段。 */
@Data @TableName("replenishment")
public class OzonBizReplenishment implements Serializable {
/** 记录ID。 */
@TableId(value="id",type=IdType.AUTO)
private Long id;
/** 来源记录ID。 */
@TableField(value="`feishu_record_id`")
private String feishuRecordId;
/** 编号。 */
@TableField(value="`replenish_no`")
private Long replenishNo;
/** 产品。 */
@TableField(value="`product_id`",updateStrategy=FieldStrategy.ALWAYS)
private Long productId;
/** 预计补货时间。 */
@TableField(value="`expected_date`",updateStrategy=FieldStrategy.ALWAYS)
private LocalDateTime expectedDate;
/** 预计补货数量。 */
@TableField(value="`expected_qty`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal expectedQty;
/** 方式。 */
@TableField(value="`method`",updateStrategy=FieldStrategy.ALWAYS)
private String method;
/** 备注。 */
@TableField(value="`remark`",updateStrategy=FieldStrategy.ALWAYS)
private String remark;
/** 创建时间。 */
@TableField(value="`created_at`")
private LocalDateTime createdAt;
/** 更新时间。 */
@TableField(value="`updated_at`")
private LocalDateTime updatedAt;
}
