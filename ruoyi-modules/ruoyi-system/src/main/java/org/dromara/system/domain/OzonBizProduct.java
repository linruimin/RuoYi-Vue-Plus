package org.dromara.system.domain;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
/** 产品基础记录，沿用来源表字段。 */
@Data @TableName("product")
public class OzonBizProduct implements Serializable {
/** 记录ID。 */
@TableId(value="id",type=IdType.AUTO)
private Long id;
/** 来源记录ID。 */
@TableField(value="`feishu_record_id`")
private String feishuRecordId;
/** 产品编号。 */
@TableField(value="`product_no`")
private Long productNo;
/** sku。 */
@TableField(value="`sku`",updateStrategy=FieldStrategy.ALWAYS)
private String sku;
/** 品名。 */
@TableField(value="`name`",updateStrategy=FieldStrategy.ALWAYS)
private String name;
/** 货号。 */
@TableField(value="`article_no`",updateStrategy=FieldStrategy.ALWAYS)
private String articleNo;
/** 店铺。 */
@TableField(value="`shop_id`",updateStrategy=FieldStrategy.ALWAYS)
private Long shopId;
/** 卖家公司名。 */
@TableField(value="`seller_company`",updateStrategy=FieldStrategy.ALWAYS)
private String sellerCompany;
/** 每包数量。 */
@TableField(value="`unit_per_pack`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal unitPerPack;
/** 后台售价。 */
@TableField(value="`backend_price`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal backendPrice;
/** 买家支付。 */
@TableField(value="`buyer_pay`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal buyerPay;
/** 到手。 */
@TableField(value="`received_price`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal receivedPrice;
/** 绿标价。 */
@TableField(value="`green_price`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal greenPrice;
/** 灰标价。 */
@TableField(value="`gray_price`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal grayPrice;
/** 划线价。 */
@TableField(value="`strike_price`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal strikePrice;
/** 父产品。 */
@TableField(value="`parent_id`",updateStrategy=FieldStrategy.ALWAYS)
private Long parentId;
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
