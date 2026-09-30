package org.dromara.system.domain.bo;
import lombok.Data;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import java.util.*;
/** 业务视图筛选参数，字段名由服务端白名单核验。 */
@Data public class OzonBusinessQuery {
 /** 首页选择的全局店铺范围，不属于单个视图的筛选配置。 */
 @Positive private Long scopeShopId;
 /** 视图精确筛选，值使用参数绑定。 */
 @Size(max=40) private Map<String,String> equals=new HashMap<>();
 /** 优先级从左到右，格式为字段:asc或字段:desc。 */
 @Size(max=500) private String sortFields;
 /** 分组键参与服务端排序，保证跨页分组连续。 */
 @Size(max=300) private String groupFields;
 /** 无分组或字段排序时，按记录的持久手动位置显示。 */
 private Boolean manualOrder;
 @Size(max=200) private String keyword;
 @Size(max=40) private Map<String,String> filters=new HashMap<>();
 @Size(max=40) private Map<String,String> ends=new HashMap<>();
}
