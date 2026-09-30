package org.dromara.system.domain.vo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizProduct;
/** 产品展示对象。 */
@Data @AutoMapper(target=OzonBizProduct.class)
public class OzonBizProductVo implements Serializable {
/** 记录ID。 */
private Long id;
/** 来源记录ID。 */
private String feishuRecordId;
/** 产品编号。 */
private Long productNo;
/** sku。 */
private String sku;
/** 品名。 */
private String name;
/** 货号。 */
private String articleNo;
/** 店铺。 */
private Long shopId;
/** 卖家公司名。 */
private String sellerCompany;
/** 每包数量。 */
private BigDecimal unitPerPack;
/** 后台售价。 */
private BigDecimal backendPrice;
/** 买家支付。 */
private BigDecimal buyerPay;
/** 到手。 */
private BigDecimal receivedPrice;
/** 绿标价。 */
private BigDecimal greenPrice;
/** 灰标价。 */
private BigDecimal grayPrice;
/** 划线价。 */
private BigDecimal strikePrice;
/** 父产品。 */
private Long parentId;
/** 备忘。 */
private String remark;
/** 创建时间。 */
private LocalDateTime createdAt;
/** 更新时间。 */
private LocalDateTime updatedAt;
/** 进货装箱总数（只读）。 */
private BigDecimal calc0;
/** 出货总数（只读）。 */
private BigDecimal calc1;
/** 总合计成本（只读）。 */
private BigDecimal calc2;
/** 后台售价总值（只读）。 */
private BigDecimal calc3;
/** 预计到手总值（只读）。 */
private BigDecimal calc4;
/** 预计到手人民币（只读）。 */
private BigDecimal calc5;
/** 预计利润（只读）。 */
private BigDecimal calc6;
/** 预计倍数（只读）。 */
private BigDecimal calc7;
/** 平均合计成本（只读）。 */
private BigDecimal calc8;
private String shopIdLabel;
private String parentIdLabel;
private String revision;
/** 附件表中的公开文件信息，仅用于图片预览。 */
private String attachmentJson;
}
