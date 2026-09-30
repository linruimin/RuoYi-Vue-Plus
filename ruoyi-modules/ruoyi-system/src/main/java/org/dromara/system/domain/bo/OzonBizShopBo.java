package org.dromara.system.domain.bo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.dromara.common.core.validate.*;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizShop;
/** 店铺写入参数。 */
@Data @AutoMapper(target=OzonBizShop.class,reverseConvertGenerate=false)
public class OzonBizShopBo implements Serializable {
@NotNull(groups=EditGroup.class) private Long id;
/** 店铺名称。 */
@NotBlank(message="店铺名称不能为空",groups={AddGroup.class,EditGroup.class})
@Size(max=64,groups={AddGroup.class,EditGroup.class})
private String name;
/** 俄文名称。 */
@Size(max=255,groups={AddGroup.class,EditGroup.class})
private String nameRu;
/** 版本指纹，防止并发覆盖。 */
@NotBlank(groups=EditGroup.class) private String revision;
}
