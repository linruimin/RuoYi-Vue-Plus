package org.dromara.system.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单费用图表数据：同一次查询给出两个维度的汇总，保证口径一致——
 * 「按月趋势」不受月份筛选影响（始终展示全部月份），「按货号排行」受月份筛选影响。
 * 金额口径统一取明细的「总计（RUB）」净额：销售额与各项应计费用正负相抵。
 */
@Data
public class OzonAccrualChartVo implements Serializable {
    /** 按应计日期所属月份汇总，供趋势柱状图；月份升序。 */
    private List<MonthStat> months;

    /** 按卖家货号归并的净金额，供排行柱状图；金额倒序（负数在后）。 */
    private List<ProductStat> products;

    /** 单个月的订单费用汇总。 */
    @Data
    public static class MonthStat implements Serializable {
        /** 应计日期所属月份（YYYY-MM）。 */
        private String month;
        /** 总计（RUB）净额。 */
        private BigDecimal totalAmountRub;
        /** 费用编号数（按 accrual_id 去重）。 */
        private Long accrualCount;
        /**
         * 订单数量：同一个应计费用编号只计一次（取该编号所有费用行 quantity 的最大值），
         * 避免一笔订单横跨「销售 / 佣金 / 配送」多行被重复累加。只有汇总图表的专用查询会填充。
         */
        private Integer totalQuantity;
    }

    /** 单个卖家货号的订单费用汇总。 */
    @Data
    public static class ProductStat implements Serializable {
        /** 卖家货号；明细没有货号时为空串，前端显示为「未标注货号」。 */
        private String sku;
        /** 产品库品名（按卖家货号关联 product.article_no）。 */
        private String productName;
        /** 明细自带的商品名称，产品库没有对应记录时兜底展示。 */
        private String ozonProductName;
        /** 总计（RUB）净额。 */
        private BigDecimal totalAmountRub;
        /** 费用编号数（按 accrual_id 去重）。 */
        private Long accrualCount;
        /** 订单数量：同一个应计费用编号只计一次（口径同 MonthStat.totalQuantity）。 */
        private Integer totalQuantity;
        /** 产品库「货品图片」的附件信息（JSON 字符串）。 */
        private String attachmentJson;
    }
}
