package org.dromara.system.domain.vo;

import lombok.Data;
import java.io.Serializable;

/** 交货报表图表数据：按卖家货号归并的产品交货数量。 */
@Data
public class OzonSupplyStatsVo implements Serializable {
    /** 品名（关联产品库中文名）。 */
    private String localProductName;

    /** 卖家货号（Ozon SKU）。 */
    private String sku;

    /** ItemCode。 */
    private String itemCode;

    /** 交货申请数（按申请内部ID去重）。 */
    private Integer orderCount;

    /** 交货数量合计。 */
    private Integer totalQuantity;

    /** 关联产品货品图片的公开文件信息。 */
    private String attachmentJson;
}
