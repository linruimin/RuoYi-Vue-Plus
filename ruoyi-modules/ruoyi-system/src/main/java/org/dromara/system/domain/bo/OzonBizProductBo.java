package org.dromara.system.domain.bo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.*;
import org.dromara.common.core.validate.*;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizProduct;
/** 产品写入参数。 */
@Data @AutoMapper(target=OzonBizProduct.class,reverseConvertGenerate=false)
public class OzonBizProductBo implements Serializable {
@NotNull(groups=EditGroup.class) private Long id;
/** sku。 */
@Size(max=64,groups={AddGroup.class,EditGroup.class})
private String sku;
/** 品名。 */
@NotBlank(message="品名不能为空",groups={AddGroup.class,EditGroup.class})
@Size(max=255,groups={AddGroup.class,EditGroup.class})
private String name;
/** 货号。 */
@Size(max=64,groups={AddGroup.class,EditGroup.class})
private String articleNo;
/** 店铺。 */
private Long shopId;
/** 卖家公司名。 */
@Size(max=255,groups={AddGroup.class,EditGroup.class})
private String sellerCompany;
/** 每包数量。 */
@NotNull(message="每包数量不能为空",groups={AddGroup.class,EditGroup.class})
@Digits(integer=10,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal unitPerPack;
/** 后台售价。 */
@Digits(integer=10,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal backendPrice;
/** 买家支付。 */
@Digits(integer=10,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal buyerPay;
/** 到手。 */
@Digits(integer=10,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal receivedPrice;
/** 绿标价。 */
@Digits(integer=10,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal greenPrice;
/** 灰标价。 */
@Digits(integer=10,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal grayPrice;
/** 划线价。 */
@Digits(integer=10,fraction=2,groups={AddGroup.class,EditGroup.class})
private BigDecimal strikePrice;
/** 父产品。 */
private Long parentId;
/** 备忘。 */
@Size(max=10000,groups={AddGroup.class,EditGroup.class})
private String remark;
/** 货品图片，随记录一起提交。 */
@Size(max=20,groups={AddGroup.class,EditGroup.class})
private List<OzonAttachmentRef> attachments;
/** 版本指纹，防止并发覆盖。 */
@NotBlank(groups=EditGroup.class) private String revision;
}
