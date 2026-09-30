package org.dromara.system.domain;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
/** 其他费用基础记录，沿用来源表字段。 */
@Data @TableName("other_fee")
public class OzonBizOtherFee implements Serializable {
/** 记录ID。 */
@TableId(value="id",type=IdType.AUTO)
private Long id;
/** 来源记录ID。 */
@TableField(value="`feishu_record_id`")
private String feishuRecordId;
/** 编号。 */
@TableField(value="`fee_no`")
private Long feeNo;
/** 费用类型。 */
@TableField(value="`fee_type`",updateStrategy=FieldStrategy.ALWAYS)
private String feeType;
/** 费用。 */
@TableField(value="`amount`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal amount;
/** 费用时间。 */
@TableField(value="`fee_date`",updateStrategy=FieldStrategy.ALWAYS)
private LocalDateTime feeDate;
/** 店铺。 */
@TableField(value="`shop_id`",updateStrategy=FieldStrategy.ALWAYS)
private Long shopId;
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
