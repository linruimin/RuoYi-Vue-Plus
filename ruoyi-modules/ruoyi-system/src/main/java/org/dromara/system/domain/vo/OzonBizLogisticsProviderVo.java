package org.dromara.system.domain.vo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizLogisticsProvider;
/** 物流商资料展示对象。 */
@Data @AutoMapper(target=OzonBizLogisticsProvider.class)
public class OzonBizLogisticsProviderVo implements Serializable {
/** 记录ID。 */
private Long id;
/** 来源记录ID。 */
private String feishuRecordId;
/** 编号。 */
private Long code;
/** 名称。 */
private String name;
/** 仓库。 */
private String warehouse;
/** 入仓费用。 */
private String warehouseFee;
/** 银行。 */
private String bank;
/** 系统。 */
private String system;
/** 客户代码。 */
private String customerCode;
/** 创建时间。 */
private LocalDateTime createdAt;
/** 更新时间。 */
private LocalDateTime updatedAt;
private String revision;
/** 附件表中的公开文件信息，仅用于图片预览。 */
private String attachmentJson;
}
