package org.dromara.system.domain.bo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.dromara.common.core.validate.*;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizStudioReceipt;
/** 工作室收货写入参数。 */
@Data @AutoMapper(target=OzonBizStudioReceipt.class,reverseConvertGenerate=false)
public class OzonBizStudioReceiptBo implements Serializable {
@NotNull(groups=EditGroup.class) private Long id;
/** 订单编号。 */
@NotBlank(message="订单编号不能为空",groups={AddGroup.class,EditGroup.class})
@Size(max=64,groups={AddGroup.class,EditGroup.class})
private String orderNo;
/** 数量。 */
@Digits(integer=10,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal quantity;
/** 收货箱数。 */
private Integer boxCount;
/** 箱规。 */
@Size(max=64,groups={AddGroup.class,EditGroup.class})
private String boxSpec;
/** 箱重/kg。 */
@Digits(integer=7,fraction=3,groups={AddGroup.class,EditGroup.class})
private BigDecimal boxWeight;
/** 收货时间。 */
private LocalDateTime receivedAt;
/** 备忘。 */
@Size(max=10000,groups={AddGroup.class,EditGroup.class})
private String remark;
/** 版本指纹，防止并发覆盖。 */
@NotBlank(groups=EditGroup.class) private String revision;
}
