package org.dromara.system.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 订单费用明细列表的一行 = 一条应计费用明细（店铺 × 应计费用编号）。
 * 数据来自**蒸馏表** {@code ozon_accrual_summary}（由 {@code ozon_accruals} 38.6 万行聚合成 7.6 万行，
 * 重建见 scripts/refresh_ozon_accrual_summary.py 与 mysql/10_ozon_accrual_summary.sql），
 * 不再对原始明细表做 GROUP BY。
 */
@Data
public class OzonAccrualSummaryVo implements Serializable {
    /** 组内最小记录编号（蒸馏表的 min_row_id），空费用编号时用于唯一定位。 */
    private String rowId;
    /** 店铺 → shop.id。 */
    private Long shopId;
    /** 店铺名称。 */
    private String shopName;
    /** 应计费用编号。 */
    private String accrualId;
    /** 同一费用编号关联的商品名称，多个名称完整去重展示。 */
    private String productName;
    /** 组内最早应计日期。 */
    private LocalDate firstAccrualDate;
    /** 组内最近应计日期，作为默认排序日期。 */
    private LocalDate accrualDate;
    /** 所有正金额之和。 */
    private BigDecimal positiveAmountRub;
    /** 所有负金额之和，保留负号。 */
    private BigDecimal negativeAmountRub;
    /** 正负金额净合计。 */
    private BigDecimal totalAmountRub;
    /** 总计减去销售额乘以0.075，不提前舍入。 */
    private BigDecimal afterTaxAmountRub;
    /** 总计-税后乘以0.075，不提前舍入。 */
    private BigDecimal totalAmountCny;
    /** 原始明细行数。 */
    private Long recordCount;
    /** 应计月份 YYYY-MM。仅「1.1.订单图表」下钻（读 ozon_accrual_item）时有值。 */
    private String statMonth;
    /** 卖家货号（TRIM 后；空串 = 「未标注货号」）。仅「1.1.订单图表」下钻时有值。 */
    private String sellerSku;
}
