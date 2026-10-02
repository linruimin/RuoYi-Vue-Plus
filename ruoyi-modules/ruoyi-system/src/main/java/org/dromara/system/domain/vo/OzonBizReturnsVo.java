package org.dromara.system.domain.vo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/** 退货展示对象：报表字段 + 关联产品库的图片/编号/品名 + 关联店铺名称。 */
@Data
public class OzonBizReturnsVo implements Serializable {
/** 关联产品的记录ID。 */
private Long productId;
/** 关联产品的产品编号。 */
private Long productNo;
/** 关联产品库的品名。 */
private String productName;
/** 关联店铺的名称。 */
private String shopIdLabel;
/** 关联产品图片（attachment 表公开文件信息），仅用于图片预览。 */
private String attachmentJson;
/** 记录ID。 */
private Long id;
/** 店铺。 */
private Long shopId;
/** 模式（FBO/FBS）。 */
private String fulfillmentScheme;
/** 货件编号。 */
private String shipmentNo;
/** 货号。 */
private String articleNo;
/** SKU。 */
private String sku;
/** 下订单日期。 */
private LocalDateTime orderDate;
/** 退货日期。 */
private LocalDateTime returnDate;
/** 状态日期。 */
private LocalDateTime statusDate;
/** 赔偿状态日期。 */
private LocalDateTime compensationDate;
/** 转到“到达取货点”日期。 */
private LocalDateTime pickupPointDate;
/** 向卖家退货日期。 */
private LocalDateTime returnToSellerDate;
/** 免费存储的最后一天。 */
private LocalDateTime freeStorageUntil;
/** 退货状态。 */
private String returnStatus;
/** 赔偿状态。 */
private String compensationStatus;
/** 标志强制性（是/否）。 */
private String mandatoryFlag;
/** 退货原因。 */
private String returnReason;
/** 买家评论。 */
private String buyerComment;
/** 买家类型。 */
private String buyerType;
/** 退货数量。 */
private Integer returnQty;
/** 货件已打开（是/否）。 */
private String packageOpened;
/** 目的地。 */
private String destination;
/** 存储地址。 */
private String storageAddress;
/** 位置。 */
private String location;
/** 存储天数。 */
private Integer storageDays;
/** 退货条形码（ii 开头）。 */
private String returnBarcode;
/** 仓储费用（卢布）。 */
private BigDecimal storageFeeRub;
/** 销毁费用（卢布）。 */
private BigDecimal disposalFeeRub;
/** 最高价格（卢布）。 */
private BigDecimal maxPriceRub;
/** Ozon 报表原始商品名称（俄文）。 */
private String ozonProductName;
/** 来源文件名。 */
private String sourceFile;
/** 导入时间。 */
private LocalDateTime importedAt;
}
