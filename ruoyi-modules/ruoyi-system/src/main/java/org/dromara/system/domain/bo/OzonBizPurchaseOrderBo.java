package org.dromara.system.domain.bo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.dromara.common.core.validate.*;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizPurchaseOrder;
/** 进货写入参数。 */
@Data @AutoMapper(target=OzonBizPurchaseOrder.class,reverseConvertGenerate=false)
public class OzonBizPurchaseOrderBo implements Serializable {
@NotNull(groups=EditGroup.class) private Long id;
/** 订单编号。 */
@NotBlank(message="订单编号不能为空",groups={AddGroup.class,EditGroup.class})
@Size(max=64,groups={AddGroup.class,EditGroup.class})
private String orderNo;
/** 产品。 */
@NotNull(message="产品不能为空",groups={AddGroup.class,EditGroup.class})
private Long productId;
/** 店铺。 */
private Long shopId;
/** 实付款(元)。 */
@Digits(integer=12,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal actualPaid;
/** 数量。 */
@Digits(integer=10,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal quantity;
/** 付款情况。 */
@Size(max=16,groups={AddGroup.class,EditGroup.class})
private String payStatus;
/** 购买。 */
@Size(max=16,groups={AddGroup.class,EditGroup.class})
private String channel;
/** 订单付款时间。 */
private LocalDateTime paidAt;
/** 备忘。 */
@Size(max=10000,groups={AddGroup.class,EditGroup.class})
private String remark;
/** 版本指纹，防止并发覆盖。 */
@NotBlank(groups=EditGroup.class) private String revision;
}
