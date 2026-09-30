package org.dromara.system.domain.vo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizOtherFee;
/** 其他费用展示对象。 */
@Data @AutoMapper(target=OzonBizOtherFee.class)
public class OzonBizOtherFeeVo implements Serializable {
/** 记录ID。 */
private Long id;
/** 来源记录ID。 */
private String feishuRecordId;
/** 编号。 */
private Long feeNo;
/** 费用类型。 */
private String feeType;
/** 费用。 */
private BigDecimal amount;
/** 费用时间。 */
private LocalDateTime feeDate;
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
