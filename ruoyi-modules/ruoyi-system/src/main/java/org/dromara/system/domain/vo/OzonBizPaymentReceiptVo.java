package org.dromara.system.domain.vo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizPaymentReceipt;
/** 回款展示对象。 */
@Data @AutoMapper(target=OzonBizPaymentReceipt.class)
public class OzonBizPaymentReceiptVo implements Serializable {
/** 记录ID。 */
private Long id;
/** 来源记录ID。 */
private String feishuRecordId;
/** 编码。 */
private Long code;
/** 日期。 */
private LocalDateTime paidAt;
/** 回款金额。 */
private BigDecimal amount;
/** 汇率。 */
private BigDecimal exchangeRate;
/** 店铺。 */
private Long shopId;
/** 备忘。 */
private String remark;
/** 创建时间。 */
private LocalDateTime createdAt;
/** 更新时间。 */
private LocalDateTime updatedAt;
private String shopIdLabel;
private String revision;
/** 附件表中的公开文件信息，仅用于图片预览。 */
private String attachmentJson;
}
