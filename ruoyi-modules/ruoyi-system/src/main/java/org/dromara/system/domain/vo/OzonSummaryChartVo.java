package org.dromara.system.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 汇总图表数据：同一次查询把「交货 / 订单 / 退货」三个主题的月度与货号汇总并到一起，
 * 供「0.0汇总图表」一张页面对比三者的走势与结构。
 * <p>
 * 口径与各自的图表页基本一致：
 * 「按月趋势」不受月份筛选影响（始终展示全部月份），「按货号排行」受月份筛选影响；
 * 交货数量按明细「完成日期」归月、退货件数按「退货日期」归月、订单数量按产品月报的统计月份归月。
 * 交货侧<b>只统计「已完成」状态</b>（与「0.1.交货图表」的默认状态一致，已取消 / 已逾期等不计入）。
 * 单位：三个主题统一为「件」——订单数量取产品月报的「售出件数」（sold_units），即该货号当月真实卖出的件数。
 * 不用应计明细按编号去重：应计明细是按「费用」铺开的，一笔销售横跨「销售收入 / 佣金 / 物流」七八行，
 * 还混着大量与卖货无关的服务费单据（合作伙伴服务 / 商品销毁 / 包装材料…），
 * 按编号去重后 76,747 里仍有 30,856 是服务费编号，且 quantity 几乎恒为 1（「数量」实际等于「笔数」）。
 */
@Data
public class OzonSummaryChartVo implements Serializable {
    /** 按月份合并的三个主题汇总，供趋势组合图；月份升序，三个指标全为空的月份会被剔除。 */
    private List<MonthStat> months;

    /** 按卖家货号归并的三个主题指标，供排行分组柱状图；三个指标全为空的货号会被剔除。 */
    private List<ProductStat> products;

    /** 单个月份的三个主题汇总；某主题该月没有数据时对应字段为空。 */
    @Data
    public static class MonthStat implements Serializable {
        /** 月份（YYYY-MM），三个主题取并集。 */
        private String month;
        /** 交货件数（归月口径＝明细完成日期，只统计「已完成」状态）。 */
        private Integer supplyQty;
        /** 交货申请数（按 order_id 去重）。 */
        private Integer supplyOrders;
        /** 订单数量：产品月报的「售出件数」（归月口径＝产品月报的统计月份）。 */
        private Integer accrualQty;
        /** 销售记录数（产品月报 sales_record_count 的合计）。 */
        private Long accrualCount;
        /** 退货件数（归月口径＝退货日期）。 */
        private Integer returnQty;
        /** 退货货件行数。 */
        private Integer returnShipments;
    }

    /** 单个卖家货号的三个主题汇总；某主题没有该货号时对应字段为空。 */
    @Data
    public static class ProductStat implements Serializable {
        /** 卖家货号；订单侧没有货号时为空串，前端显示为「未标注货号」。 */
        private String sku;
        /** 产品库中文品名（三个主题谁先取到用谁）。 */
        private String productName;
        /** 产品库「货品图片」的附件信息（JSON 字符串）。 */
        private String attachmentJson;
        /** 交货件数（该货号「已完成」状态的合计，与 Chart 默认口径一致）。 */
        private Integer supplyQty;
        /** 交货申请数（按 order_id 去重）。 */
        private Integer supplyOrders;
        /** 订单数量：产品月报的「售出件数」（口径同 MonthStat.accrualQty）。 */
        private Integer accrualQty;
        /** 销售记录数（产品月报 sales_record_count 的合计）。 */
        private Long accrualCount;
        /** 退货件数。 */
        private Integer returnQty;
        /** 退货货件行数。 */
        private Integer returnShipments;
    }
}
