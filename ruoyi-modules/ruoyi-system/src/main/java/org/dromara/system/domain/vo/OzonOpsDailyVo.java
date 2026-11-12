package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日运营简报的**摘要**（列表行，不含正文）。
 * <p>
 * 对应 {@code ozon_ops_daily} 一行 = 一个莫斯科自然日，双店合并。
 * 金额单位一律卢布（₽）。
 */
@Data
public class OzonOpsDailyVo implements Serializable {
    private Long id;
    private LocalDate statDate;
    /** 标题：一句话概括当天，如「11.11 双店同创新高」。 */
    private String title;
    /** 一句话结论。 */
    private String digest;
    private Integer orders;
    private Integer units;
    private BigDecimal revenue;
    private Integer cancelled;
    /** 客单价 ₽。 */
    private BigDecimal aov;
    /** 前一日订单数（环比用）。 */
    private Integer ordersPrev;
    private BigDecimal revenuePrev;
    /** 前 7 日日均订单（不含当天）。 */
    private BigDecimal ordersBase7;
    private BigDecimal revenueBase7;
    private Integer shop1Orders;
    private BigDecimal shop1Revenue;
    private Integer shop2Orders;
    private BigDecimal shop2Revenue;
    /** 当日调价 SKU 数。 */
    private Integer priceChanges;
    /** 进行中的促销活动数（双店合计）。 */
    private Integer promoActive;
    /** 当日广告花费 ₽（双店合计）。 */
    private BigDecimal adSpent;
    /** 简报里列出的风险条数。 */
    private Integer riskCount;
    private LocalDateTime generatedAt;
}
