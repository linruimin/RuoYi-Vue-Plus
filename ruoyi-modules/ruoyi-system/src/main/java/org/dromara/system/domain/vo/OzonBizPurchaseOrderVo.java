package org.dromara.system.domain.vo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizPurchaseOrder;
/** 进货展示对象。 */
@Data @AutoMapper(target=OzonBizPurchaseOrder.class)
public class OzonBizPurchaseOrderVo implements Serializable {
/** 记录ID。 */
private Long id;
/** 来源记录ID。 */
private String feishuRecordId;
/** 订单编号。 */
private String orderNo;
/** 产品。 */
private Long productId;
/** 店铺。 */
private Long shopId;
/** 实付款(元)。 */
private BigDecimal actualPaid;
/** 数量。 */
private BigDecimal quantity;
/** 付款情况。 */
private String payStatus;
/** 购买。 */
private String channel;
/** 订单付款时间。 */
private LocalDateTime paidAt;
/** 创建时间。 */
private LocalDateTime sourceCreatedAt;
/** 备忘。 */
private String remark;
/** 创建时间。 */
private LocalDateTime createdAt;
/** 更新时间。 */
private LocalDateTime updatedAt;
/** 每包数量（只读）。 */
private BigDecimal calc0;
/** 单个成本（只读）。 */
private BigDecimal calc1;
/** 装箱总数（只读）。 */
private BigDecimal calc2;
/** 每包成本（只读）。 */
private BigDecimal calc3;
private String productIdLabel;
private String shopIdLabel;
private String revision;
/** 附件表中的公开文件信息，仅用于图片预览。 */
private String attachmentJson;
}
