package org.dromara.system.domain;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
/** 进货基础记录，沿用来源表字段。 */
@Data @TableName("purchase_order")
public class OzonBizPurchaseOrder implements Serializable {
/** 记录ID。 */
@TableId(value="id",type=IdType.AUTO)
private Long id;
/** 来源记录ID。 */
@TableField(value="`feishu_record_id`")
private String feishuRecordId;
/** 订单编号。 */
@TableField(value="`order_no`",updateStrategy=FieldStrategy.ALWAYS)
private String orderNo;
/** 产品。 */
@TableField(value="`product_id`",updateStrategy=FieldStrategy.ALWAYS)
private Long productId;
/** 店铺。 */
@TableField(value="`shop_id`",updateStrategy=FieldStrategy.ALWAYS)
private Long shopId;
/** 实付款(元)。 */
@TableField(value="`actual_paid`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal actualPaid;
/** 数量。 */
@TableField(value="`quantity`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal quantity;
/** 付款情况。 */
@TableField(value="`pay_status`",updateStrategy=FieldStrategy.ALWAYS)
private String payStatus;
/** 购买。 */
@TableField(value="`channel`",updateStrategy=FieldStrategy.ALWAYS)
private String channel;
/** 订单付款时间。 */
@TableField(value="`paid_at`",updateStrategy=FieldStrategy.ALWAYS)
private LocalDateTime paidAt;
/** 创建时间。 */
@TableField(value="`source_created_at`")
private LocalDateTime sourceCreatedAt;
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
