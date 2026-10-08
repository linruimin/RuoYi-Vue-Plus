package org.dromara.system.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 退货报表图表数据：同一次查询给出两个维度的汇总，保证口径一致——
 * 「按月趋势」不受月份筛选影响（始终展示全部月份），「按产品排行」受月份筛选影响。
 */
@Data
public class OzonReturnsChartVo implements Serializable {
    /** 按退货月份汇总，供趋势柱状图；月份升序。 */
    private List<MonthStat> months;

    /** 按卖家货号归并的产品退货件数，供排行柱状图；退货件数倒序。 */
    private List<ProductStat> products;

    /** 单个月份的退货汇总。 */
    @Data
    public static class MonthStat implements Serializable {
        /** 退货月份（YYYY-MM）。 */
        private String month;
        /** 退货件数合计。 */
        private Integer returnQty;
        /** 退货货件行数。 */
        private Integer shipmentCount;
    }

    /** 单个产品的退货汇总。 */
    @Data
    public static class ProductStat implements Serializable {
        /** 品名（关联产品库中文名）。 */
        private String localProductName;
        /** 卖家货号。 */
        private String articleNo;
        /** Ozon SKU。 */
        private String sku;
        /** 退货货件行数。 */
        private Integer shipmentCount;
        /** 退货件数合计。 */
        private Integer returnQty;
        /** 关联产品货品图片的公开文件信息。 */
        private String attachmentJson;
    }
}
