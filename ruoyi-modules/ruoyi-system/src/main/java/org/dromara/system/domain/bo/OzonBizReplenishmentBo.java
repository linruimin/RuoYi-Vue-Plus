package org.dromara.system.domain.bo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.dromara.common.core.validate.*;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizReplenishment;
/** 补货写入参数。 */
@Data @AutoMapper(target=OzonBizReplenishment.class,reverseConvertGenerate=false)
public class OzonBizReplenishmentBo implements Serializable {
@NotNull(groups=EditGroup.class) private Long id;
/** 产品。 */
@NotNull(message="产品不能为空",groups={AddGroup.class,EditGroup.class})
private Long productId;
/** 预计补货时间。 */
private LocalDateTime expectedDate;
/** 预计补货数量。 */
@Digits(integer=10,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal expectedQty;
/** 方式。 */
@Size(max=32,groups={AddGroup.class,EditGroup.class})
private String method;
/** 备注。 */
@Size(max=10000,groups={AddGroup.class,EditGroup.class})
private String remark;
/** 版本指纹，防止并发覆盖。 */
@NotBlank(groups=EditGroup.class) private String revision;
}
