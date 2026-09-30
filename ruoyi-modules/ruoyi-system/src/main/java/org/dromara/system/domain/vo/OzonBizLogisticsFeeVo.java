package org.dromara.system.domain.vo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizLogisticsFee;
/** 物流费用展示对象。 */
@Data @AutoMapper(target=OzonBizLogisticsFee.class)
public class OzonBizLogisticsFeeVo implements Serializable {
/** 记录ID。 */
private Long id;
/** 来源记录ID。 */
private String feishuRecordId;
/** 物流费用编号。 */
private Long feeNo;
/** 费用类型。 */
private String feeType;
/** 费用。 */
private BigDecimal amount;
/** 是否已付。 */
private String paidFlag;
/** 费用时间。 */
private LocalDateTime feeDate;
/** 店铺。 */
private Long shopId;
/** 费用备注。 */
private String remark;
/** 创建时间。 */
private LocalDateTime createdAt;
/** 更新时间。 */
private LocalDateTime updatedAt;
/** 总重量（只读）。 */
private BigDecimal calc0;
/** 总体积（只读）。 */
private BigDecimal calc1;
/** 单体积费用（只读）。 */
private BigDecimal calc2;
/** 单重量费用（只读）。 */
private BigDecimal calc3;
private String shopIdLabel;
private java.util.List<Long> shipmentIds;
private String shipmentIdsLabel;
private String revision;
/** 附件表中的公开文件信息，仅用于图片预览。 */
private String attachmentJson;
}
