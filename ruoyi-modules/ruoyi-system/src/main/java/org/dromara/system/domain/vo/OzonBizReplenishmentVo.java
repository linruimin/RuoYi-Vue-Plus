package org.dromara.system.domain.vo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizReplenishment;
/** 补货展示对象。 */
@Data @AutoMapper(target=OzonBizReplenishment.class)
public class OzonBizReplenishmentVo implements Serializable {
/** 记录ID。 */
private Long id;
/** 来源记录ID。 */
private String feishuRecordId;
/** 编号。 */
private Long replenishNo;
/** 产品。 */
private Long productId;
/** 预计补货时间。 */
private LocalDateTime expectedDate;
/** 预计补货数量。 */
private BigDecimal expectedQty;
/** 方式。 */
private String method;
/** 备注。 */
private String remark;
/** 创建时间。 */
private LocalDateTime createdAt;
/** 更新时间。 */
private LocalDateTime updatedAt;
private String productIdLabel;
private String revision;
/** 附件表中的公开文件信息，仅用于图片预览。 */
private String attachmentJson;
}
