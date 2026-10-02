package org.dromara.system.domain.bo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.*;
import org.dromara.common.core.validate.*;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizLogisticsFee;
/** 物流费用写入参数。 */
@Data @AutoMapper(target=OzonBizLogisticsFee.class,reverseConvertGenerate=false)
public class OzonBizLogisticsFeeBo implements Serializable {
@NotNull(groups=EditGroup.class) private Long id;
/** 费用类型。 */
@Size(max=32,groups={AddGroup.class,EditGroup.class})
private String feeType;
/** 费用。 */
@Digits(integer=12,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal amount;
/** 是否已付。 */
@Size(max=8,groups={AddGroup.class,EditGroup.class})
private String paidFlag;
/** 费用时间。 */
private LocalDateTime feeDate;
/** 店铺。 */
private Long shopId;
/** 费用备注。 */
@Size(max=10000,groups={AddGroup.class,EditGroup.class})
private String remark;
@Size(max=500,groups={AddGroup.class,EditGroup.class}) private java.util.List<@NotNull Long> shipmentIds;
/** 费用凭证图片，随记录一起提交。 */
@Size(max=20,groups={AddGroup.class,EditGroup.class})
private List<OzonAttachmentRef> attachments;
/** 版本指纹，防止并发覆盖。 */
@NotBlank(groups=EditGroup.class) private String revision;
}
