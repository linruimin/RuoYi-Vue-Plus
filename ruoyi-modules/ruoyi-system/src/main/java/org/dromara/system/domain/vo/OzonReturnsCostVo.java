package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 退货成本视图：把「退货单」与「应计费用」并到一张表上，看清每单退货的真实成本。
 *
 * <p>口径说明（重要）：
 * <ul>
 *   <li>退货侧来自 ozon_returns（未完结退货快照），费用列只有仓储费 / 销毁费；</li>
 *   <li>成本侧来自 ozon_accruals 的 3 个退货科目：逆向物流、合作伙伴处理退货、自提点退货受理；</li>
 *   <li>应计没有货件号，只能按「卖家货号 + 订单日期」分组，把组内金额**均摊**到该组的退货单上；</li>
 *   <li>无订单日期的应计行无法归属，单独计入 {@code unmappedCount / unmappedCostRub}，不计入单均成本。</li>
 * </ul>
 */
@Data
public class OzonReturnsCostVo implements Serializable {

    /** 汇总指标。 */
    private Summary summary;

    /** 按卖家货号归并的成本汇总（成本倒序）。 */
    private List<ArticleStat> articles;

    /** 退货明细行（含每单分摊成本）。 */
    private List<CostRow> rows;

    /** 汇总卡片。成本字段均为**正数**（表示花了多少钱）。 */
    @Data
    public static class Summary implements Serializable {
        /** 退货货件行数。 */
        private Integer shipmentCount;
        /** 退货件数。 */
        private Integer returnQty;
        /** 退货货值合计（ozon_returns.max_price_rub）。 */
        private BigDecimal goodsValueRub;
        /** 退货报告里的仓储费。 */
        private BigDecimal storageFeeRub;
        /** 退货报告里的销毁费。 */
        private BigDecimal disposalFeeRub;
        /** 逆向物流（应计，已分摊）。 */
        private BigDecimal reverseLogisticsRub;
        /** 合作伙伴处理退货、取消和拒收（应计，已分摊）。 */
        private BigDecimal handlingFeeRub;
        /** 自提点退货受理（应计，已分摊）。 */
        private BigDecimal pickupFeeRub;
        /** 上述三项成本合计。 */
        private BigDecimal costTotalRub;
        /** 单均成本 = 成本合计 / 退货单数。 */
        private BigDecimal costPerShipmentRub;
        /** 费用率 = 成本合计 / 退货货值，单位 %。 */
        private BigDecimal costRatePct;
        /** 收入退回（退给买家的货款，负数表示冲减收入）。 */
        private BigDecimal revenueRefundRub;
        /** 佣金返还（Ozon 退回的佣金，正数）。 */
        private BigDecimal commissionRefundRub;
        /** 无法按「货号 + 订单日」归属的应计笔数。 */
        private Integer unmappedCount;
        /** 无法归属的应计金额（正数）。 */
        private BigDecimal unmappedCostRub;
    }

    /** 单个卖家货号的成本汇总。 */
    @Data
    public static class ArticleStat implements Serializable {
        /** 卖家货号。 */
        private String articleNo;
        /** 品名（产品库中文名，缺失时回落到 Ozon 品名）。 */
        private String productName;
        /** 产品货品首图 URL（直接给前端渲染，避免逐行重复大 JSON）。 */
        private String imageUrl;
        /** 退货单数。 */
        private Integer shipmentCount;
        /** 退货件数。 */
        private Integer returnQty;
        /** 逆向物流成本（正数）。 */
        private BigDecimal reverseLogisticsRub;
        /** 退货处理费（正数）。 */
        private BigDecimal handlingFeeRub;
        /** 自提点受理费（正数）。 */
        private BigDecimal pickupFeeRub;
        /** 成本合计。 */
        private BigDecimal costTotalRub;
        /** 单均成本。 */
        private BigDecimal costPerShipmentRub;
    }

    /** 一行 = 一张退货单（含分摊到该单的应计成本）。 */
    @Data
    public static class CostRow implements Serializable {
        /** 店铺 ID，用于和应计分组对齐（分摊键）。 */
        private Long shopId;
        /** 货件编号。 */
        private String shipmentNo;
        /** 卖家货号。 */
        private String articleNo;
        /** Ozon SKU。 */
        private String sku;
        /** 品名（产品库中文名优先）。 */
        private String productName;
        /** 产品货品首图 URL（直接给前端渲染，避免逐行重复大 JSON）。 */
        private String imageUrl;
        /** 店铺名。 */
        private String shopName;
        /** 退货状态。 */
        private String returnStatus;
        /** 退货日期 YYYY-MM-DD HH:mm:ss。 */
        private String returnDate;
        /** 订单日期 YYYY-MM-DD，用于和应计分组对齐（分摊键）。 */
        private String orderDate;
        /** 状态日期 YYYY-MM-DD HH:mm:ss。 */
        private String statusDate;
        /** 仓储天数（Ozon 报告值）。 */
        private Integer storageDays;
        /** 退货件数。 */
        private Integer returnQty;
        /** 退货报告仓储费。 */
        private BigDecimal storageFeeRub;
        /** 分摊的逆向物流成本（正数）。 */
        private BigDecimal reverseLogisticsRub;
        /** 分摊的退货处理费（正数）。 */
        private BigDecimal handlingFeeRub;
        /** 分摊的自提点受理费（正数）。 */
        private BigDecimal pickupFeeRub;
        /** 该单成本合计（正数）。 */
        private BigDecimal costTotalRub;
    }
}
