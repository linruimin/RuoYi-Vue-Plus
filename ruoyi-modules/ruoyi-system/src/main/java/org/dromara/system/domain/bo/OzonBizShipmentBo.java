package org.dromara.system.domain.bo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.dromara.common.core.validate.*;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizShipment;
/** 出货写入参数。 */
@Data @AutoMapper(target=OzonBizShipment.class,reverseConvertGenerate=false)
public class OzonBizShipmentBo implements Serializable {
@NotNull(groups=EditGroup.class) private Long id;
/** 进货订单。 */
@NotNull(message="进货订单不能为空",groups={AddGroup.class,EditGroup.class})
private Long purchaseId;
/** 店铺。 */
private Long shopId;
/** 物流方式。 */
@Size(max=32,groups={AddGroup.class,EditGroup.class})
private String logisticsMethod;
/** 货物情况。 */
@Size(max=32,groups={AddGroup.class,EditGroup.class})
private String goodsStatus;
/** 箱规。 */
@Size(max=64,groups={AddGroup.class,EditGroup.class})
private String boxSpec;
/** 箱数。 */
private Integer boxes;
/** 每箱数量。 */
@Digits(integer=10,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal perBoxQty;
/** 单箱重/kg。 */
@Digits(integer=7,fraction=3,groups={AddGroup.class,EditGroup.class})
private BigDecimal boxWeight;
/** 入仓单号。 */
@Size(max=64,groups={AddGroup.class,EditGroup.class})
private String warehouseInNo;
/** 跨境物流唛。 */
@Size(max=255,groups={AddGroup.class,EditGroup.class})
private String shippingMark;
/** 单箱打包费用。 */
@Digits(integer=12,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal perBoxPackingFee;
/** 单箱店铺费用+购买费用。 */
@Digits(integer=12,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal perBoxShopBuyFee;
/** 单箱取货送仓费用。 */
@Digits(integer=12,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal perBoxPickupFee;
/** 跨境运输费用/美元/kg。 */
@Digits(integer=6,fraction=4,groups={AddGroup.class,EditGroup.class})
private BigDecimal crossRateUsdPerKg;
/** 出货时间。 */
private LocalDateTime shippedAt;
/** 入仓时间。 */
private LocalDateTime warehouseInAt;
@Size(max=500,groups={AddGroup.class,EditGroup.class}) private java.util.List<@NotNull Long> logisticsFeeIds;
/** 版本指纹，防止并发覆盖。 */
@NotBlank(groups=EditGroup.class) private String revision;
}
