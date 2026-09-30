package org.dromara.system.domain;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
/** 物流商资料基础记录，沿用来源表字段。 */
@Data @TableName("logistics_provider")
public class OzonBizLogisticsProvider implements Serializable {
/** 记录ID。 */
@TableId(value="id",type=IdType.AUTO)
private Long id;
/** 来源记录ID。 */
@TableField(value="`feishu_record_id`")
private String feishuRecordId;
/** 编号。 */
@TableField(value="`code`")
private Long code;
/** 名称。 */
@TableField(value="`name`",updateStrategy=FieldStrategy.ALWAYS)
private String name;
/** 仓库。 */
@TableField(value="`warehouse`",updateStrategy=FieldStrategy.ALWAYS)
private String warehouse;
/** 入仓费用。 */
@TableField(value="`warehouse_fee`",updateStrategy=FieldStrategy.ALWAYS)
private String warehouseFee;
/** 银行。 */
@TableField(value="`bank`",updateStrategy=FieldStrategy.ALWAYS)
private String bank;
/** 系统。 */
@TableField(value="`system`",updateStrategy=FieldStrategy.ALWAYS)
private String system;
/** 客户代码。 */
@TableField(value="`customer_code`",updateStrategy=FieldStrategy.ALWAYS)
private String customerCode;
/** 创建时间。 */
@TableField(value="`created_at`")
private LocalDateTime createdAt;
/** 更新时间。 */
@TableField(value="`updated_at`")
private LocalDateTime updatedAt;
}
