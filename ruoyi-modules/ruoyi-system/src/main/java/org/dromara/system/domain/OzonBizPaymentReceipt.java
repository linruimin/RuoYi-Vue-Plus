package org.dromara.system.domain;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
/** 回款基础记录，沿用来源表字段。 */
@Data @TableName("payment_receipt")
public class OzonBizPaymentReceipt implements Serializable {
/** 记录ID。 */
@TableId(value="id",type=IdType.AUTO)
private Long id;
/** 来源记录ID。 */
@TableField(value="`feishu_record_id`")
private String feishuRecordId;
/** 编码。 */
@TableField(value="`code`")
private Long code;
/** 日期。 */
@TableField(value="`paid_at`",updateStrategy=FieldStrategy.ALWAYS)
private LocalDateTime paidAt;
/** 回款金额。 */
@TableField(value="`amount`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal amount;
/** 汇率。 */
@TableField(value="`exchange_rate`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal exchangeRate;
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
