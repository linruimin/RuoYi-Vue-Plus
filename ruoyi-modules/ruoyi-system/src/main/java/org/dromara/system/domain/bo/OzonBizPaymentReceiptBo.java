package org.dromara.system.domain.bo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.dromara.common.core.validate.*;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizPaymentReceipt;
/** 回款写入参数。 */
@Data @AutoMapper(target=OzonBizPaymentReceipt.class,reverseConvertGenerate=false)
public class OzonBizPaymentReceiptBo implements Serializable {
@NotNull(groups=EditGroup.class) private Long id;
/** 日期。 */
private LocalDateTime paidAt;
/** 回款金额。 */
@Digits(integer=12,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal amount;
/** 汇率。 */
@Digits(integer=6,fraction=4,groups={AddGroup.class,EditGroup.class})
private BigDecimal exchangeRate;
/** 店铺。 */
private Long shopId;
/** 备忘。 */
@Size(max=10000,groups={AddGroup.class,EditGroup.class})
private String remark;
/** 版本指纹，防止并发覆盖。 */
@NotBlank(groups=EditGroup.class) private String revision;
}
