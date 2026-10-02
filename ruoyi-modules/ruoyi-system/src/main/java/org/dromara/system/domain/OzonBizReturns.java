package org.dromara.system.domain;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
/** 退货基础记录，数据来自 Ozon 后台报表同步（只读）。 */
@Data @TableName("returns")
public class OzonBizReturns implements Serializable {
/** 记录ID（对应 ozon_returns.row_id）。 */
@TableId(value="id",type=IdType.AUTO)
private Long id;
/** 店铺。 */
@TableField(value="`shop_id`")
private Long shopId;
/** 模式（FBO/FBS）。 */
@TableField(value="`fulfillment_scheme`")
private String fulfillmentScheme;
/** 货件编号。 */
@TableField(value="`shipment_no`")
private String shipmentNo;
/** 货号。 */
@TableField(value="`article_no`")
private String articleNo;
/** SKU。 */
@TableField(value="`sku`")
private String sku;
/** 下订单日期。 */
@TableField(value="`order_date`")
private LocalDateTime orderDate;
/** 退货日期。 */
@TableField(value="`return_date`")
private LocalDateTime returnDate;
/** 状态日期。 */
@TableField(value="`status_date`")
private LocalDateTime statusDate;
/** 赔偿状态日期。 */
@TableField(value="`compensation_date`")
private LocalDateTime compensationDate;
/** 转到“到达取货点”日期。 */
@TableField(value="`pickup_point_date`")
private LocalDateTime pickupPointDate;
/** 向卖家退货日期。 */
@TableField(value="`return_to_seller_date`")
private LocalDateTime returnToSellerDate;
/** 免费存储的最后一天。 */
@TableField(value="`free_storage_until`")
private LocalDateTime freeStorageUntil;
/** 退货状态。 */
@TableField(value="`return_status`")
private String returnStatus;
/** 赔偿状态。 */
@TableField(value="`compensation_status`")
private String compensationStatus;
/** 标志强制性（是/否）。 */
@TableField(value="`mandatory_flag`")
private String mandatoryFlag;
/** 退货原因。 */
@TableField(value="`return_reason`")
private String returnReason;
/** 买家评论。 */
@TableField(value="`buyer_comment`")
private String buyerComment;
/** 买家类型。 */
@TableField(value="`buyer_type`")
private String buyerType;
/** 退货数量。 */
@TableField(value="`return_qty`")
private Integer returnQty;
/** 货件已打开（是/否）。 */
@TableField(value="`package_opened`")
private String packageOpened;
/** 目的地。 */
@TableField(value="`destination`")
private String destination;
/** 存储地址。 */
@TableField(value="`storage_address`")
private String storageAddress;
/** 位置。 */
@TableField(value="`location`")
private String location;
/** 存储天数。 */
@TableField(value="`storage_days`")
private Integer storageDays;
/** 退货条形码（ii 开头）。 */
@TableField(value="`return_barcode`")
private String returnBarcode;
/** 仓储费用（卢布）。 */
@TableField(value="`storage_fee_rub`")
private BigDecimal storageFeeRub;
/** 销毁费用（卢布）。 */
@TableField(value="`disposal_fee_rub`")
private BigDecimal disposalFeeRub;
/** 最高价格（卢布）。 */
@TableField(value="`max_price_rub`")
private BigDecimal maxPriceRub;
/** Ozon 报表原始商品名称（俄文，仅供核对；页面默认展示的是关联产品库的品名）。 */
@TableField(value="`ozon_product_name`")
private String ozonProductName;
/** 来源文件名。 */
@TableField(value="`source_file`")
private String sourceFile;
/** 导入时间。 */
@TableField(value="`imported_at`")
private LocalDateTime importedAt;
}
