package org.dromara.system.domain;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
/** 出货基础记录，沿用来源表字段。 */
@Data @TableName("shipment")
public class OzonBizShipment implements Serializable {
/** 记录ID。 */
@TableId(value="id",type=IdType.AUTO)
private Long id;
/** 来源记录ID。 */
@TableField(value="`feishu_record_id`")
private String feishuRecordId;
/** 出货编号。 */
@TableField(value="`shipment_no`")
private Long shipmentNo;
/** 进货订单。 */
@TableField(value="`purchase_id`",updateStrategy=FieldStrategy.ALWAYS)
private Long purchaseId;
/** 店铺。 */
@TableField(value="`shop_id`",updateStrategy=FieldStrategy.ALWAYS)
private Long shopId;
/** 物流方式。 */
@TableField(value="`logistics_method`",updateStrategy=FieldStrategy.ALWAYS)
private String logisticsMethod;
/** 货物情况。 */
@TableField(value="`goods_status`",updateStrategy=FieldStrategy.ALWAYS)
private String goodsStatus;
/** 箱规。 */
@TableField(value="`box_spec`",updateStrategy=FieldStrategy.ALWAYS)
private String boxSpec;
/** 箱数。 */
@TableField(value="`boxes`",updateStrategy=FieldStrategy.ALWAYS)
private Integer boxes;
/** 每箱数量。 */
@TableField(value="`per_box_qty`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal perBoxQty;
/** 单箱重/kg。 */
@TableField(value="`box_weight`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal boxWeight;
/** 入仓单号。 */
@TableField(value="`warehouse_in_no`",updateStrategy=FieldStrategy.ALWAYS)
private String warehouseInNo;
/** 跨境物流唛。 */
@TableField(value="`shipping_mark`",updateStrategy=FieldStrategy.ALWAYS)
private String shippingMark;
/** 单箱打包费用。 */
@TableField(value="`per_box_packing_fee`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal perBoxPackingFee;
/** 单箱店铺费用+购买费用。 */
@TableField(value="`per_box_shop_buy_fee`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal perBoxShopBuyFee;
/** 单箱取货送仓费用。 */
@TableField(value="`per_box_pickup_fee`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal perBoxPickupFee;
/** 跨境运输费用/美元/kg。 */
@TableField(value="`cross_rate_usd_per_kg`",updateStrategy=FieldStrategy.ALWAYS)
private BigDecimal crossRateUsdPerKg;
/** 出货时间。 */
@TableField(value="`shipped_at`",updateStrategy=FieldStrategy.ALWAYS)
private LocalDateTime shippedAt;
/** 入仓时间。 */
@TableField(value="`warehouse_in_at`",updateStrategy=FieldStrategy.ALWAYS)
private LocalDateTime warehouseInAt;
/** 创建时间。 */
@TableField(value="`created_at`")
private LocalDateTime createdAt;
/** 更新时间。 */
@TableField(value="`updated_at`")
private LocalDateTime updatedAt;
}
