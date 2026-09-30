package org.dromara.system.domain.vo;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.system.domain.OzonBizShop;
/** 店铺展示对象。 */
@Data @AutoMapper(target=OzonBizShop.class)
public class OzonBizShopVo implements Serializable {
/** 记录ID。 */
private Long id;
/** 来源记录ID。 */
private String feishuRecordId;
/** 店铺名称。 */
private String name;
/** 俄文名称。 */
private String nameRu;
/** 创建时间。 */
private LocalDateTime createdAt;
/** 更新时间。 */
private LocalDateTime updatedAt;
private String revision;
}
