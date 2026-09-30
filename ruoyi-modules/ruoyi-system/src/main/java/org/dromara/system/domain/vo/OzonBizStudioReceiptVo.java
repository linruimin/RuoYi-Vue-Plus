package org.dromara.system.domain.vo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizStudioReceipt;
/** 工作室收货展示对象。 */
@Data @AutoMapper(target=OzonBizStudioReceipt.class)
public class OzonBizStudioReceiptVo implements Serializable {
/** 进货订单对应的产品名称。 */
private String productName;
/** 记录ID。 */
private Long id;
/** 来源记录ID。 */
private String feishuRecordId;
/** 订单编号。 */
private String orderNo;
/** 数量。 */
private BigDecimal quantity;
/** 收货箱数。 */
private Integer boxCount;
/** 箱规。 */
private String boxSpec;
/** 箱重/kg。 */
private BigDecimal boxWeight;
/** 收货时间。 */
private LocalDateTime receivedAt;
/** 备忘。 */
private String remark;
/** 创建时间。 */
private LocalDateTime createdAt;
/** 更新时间。 */
private LocalDateTime updatedAt;
/** 收货每箱数量（只读）。 */
private BigDecimal calc0;
/** 箱体积/m3（只读）。 */
private BigDecimal calc1;
/** 总箱重/kg（只读）。 */
private BigDecimal calc2;
private String revision;
/** 附件表中的公开文件信息，仅用于图片预览。 */
private String attachmentJson;
}
