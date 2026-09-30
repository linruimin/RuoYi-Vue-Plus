package org.dromara.system.domain;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
/** 工作室收货基础记录，沿用来源表字段。 */
@Data @TableName("studio_receipt")
public class OzonBizStudioReceipt implements Serializable {
/** 记录ID。 */
@TableId(value="id",type=IdType.AUTO)
private Long id;
/** 来源记录ID。 */
@TableField(value="`feishu_record_id`")
private String feishuRecordId;
/** 订单编号。 */
@TableField(value="`order_no`",updateStrategy=FieldStrategy.ALWAYS)
private String orderNo;
/** 数量。 */
@TableField(value="`quantity`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal quantity;
/** 收货箱数。 */
@TableField(value="`box_count`",updateStrategy=FieldStrategy.ALWAYS)
private Integer boxCount;
/** 箱规。 */
@TableField(value="`box_spec`",updateStrategy=FieldStrategy.ALWAYS)
private String boxSpec;
/** 箱重/kg。 */
@TableField(value="`box_weight`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal boxWeight;
/** 收货时间。 */
@TableField(value="`received_at`",updateStrategy=FieldStrategy.ALWAYS)
private LocalDateTime receivedAt;
/** 备忘。 */
@TableField(value="`remark`",updateStrategy=FieldStrategy.ALWAYS)
private String remark;
/** 创建时间。 */
@TableField(value="`created_at`")
private LocalDateTime createdAt;
/** 更新时间。 */
@TableField(value="`updated_at`")
private LocalDateTime updatedAt;
}
