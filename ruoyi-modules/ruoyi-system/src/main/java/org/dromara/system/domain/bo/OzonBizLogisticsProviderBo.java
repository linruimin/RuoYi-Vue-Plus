package org.dromara.system.domain.bo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.*;
import org.dromara.common.core.validate.*;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizLogisticsProvider;
/** 物流商资料写入参数。 */
@Data @AutoMapper(target=OzonBizLogisticsProvider.class,reverseConvertGenerate=false)
public class OzonBizLogisticsProviderBo implements Serializable {
@NotNull(groups=EditGroup.class) private Long id;
/** 名称。 */
@Size(max=255,groups={AddGroup.class,EditGroup.class})
private String name;
/** 仓库。 */
@Size(max=10000,groups={AddGroup.class,EditGroup.class})
private String warehouse;
/** 入仓费用。 */
@Size(max=255,groups={AddGroup.class,EditGroup.class})
private String warehouseFee;
/** 银行。 */
@Size(max=255,groups={AddGroup.class,EditGroup.class})
private String bank;
/** 系统。 */
@Size(max=255,groups={AddGroup.class,EditGroup.class})
private String system;
/** 客户代码。 */
@Size(max=255,groups={AddGroup.class,EditGroup.class})
private String customerCode;
/** 跨境运费凭证图片，随记录一起提交。 */
@Size(max=20,groups={AddGroup.class,EditGroup.class})
private List<OzonAttachmentRef> attachments;
/** 版本指纹，防止并发覆盖。 */
@NotBlank(groups=EditGroup.class) private String revision;
}
