package org.dromara.system.service;

import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.system.domain.bo.OzonReportQuery;
import org.dromara.system.domain.vo.*;

/** Ozon 订单报告只读查询。 */
public interface IOzonReportService {
    /** 按月份或卖家SKU分组排序产品月报。 */
    PageResult<OzonMonthlyReportVo> monthly(OzonReportQuery query, PageQuery page);
    /** 按费用编号合计所有正数、负数和净额。 */
    PageResult<OzonAccrualSummaryVo> accruals(OzonReportQuery query, PageQuery page);
    /** 查看一个费用编号的全部原始明细。 */
    PageResult<OzonAccrualReportVo> accrualLines(OzonReportQuery query, PageQuery page);
    /** 查询已完成的交货申请商品明细。 */
    PageResult<OzonSupplyReportVo> supply(OzonReportQuery query, PageQuery page);
    /** 按「退货月份 × SKU」汇总退货件数、费用与退货率。 */
    PageResult<OzonReturnsReportVo> returnsReport(OzonReportQuery query, PageQuery page);
    /** 查询当前费用视图全部结果的产品月度销售趋势，不受分页影响。 */
    java.util.List<OzonProductSalesTrendVo> salesTrend(OzonReportQuery query);
    /** 当前费用筛选范围内的产品选项。 */
    java.util.List<OzonSalesProductVo> salesProducts(OzonReportQuery query);
    /** 查询所选产品的全部逐笔销售额。 */
    java.util.List<OzonProductSaleVo> salesTransactions(OzonReportQuery query);
    /** 交货图表：按月趋势 + 按产品排行，一次查询保证口径一致。 */
    OzonSupplyChartVo supplyChart(Long scopeShopId, String status, String month);
    /** 交货图表下钻：某个卖家货号或某个月份下的交货申请明细行。 */
    java.util.List<OzonSupplyReportVo> supplyProductRows(Long scopeShopId, String status, String sku, String month);

    /** 退货图表：按月趋势 + 按产品排行，一次查询保证口径一致。 */
    OzonReturnsChartVo returnsChart(Long scopeShopId, String status, String month);

    /** 退货图表下钻：某个卖家货号或某个月份下的退货明细行。 */
    java.util.List<OzonBizReturnsVo> returnsChartRows(Long scopeShopId, String status, String month, String articleNo);

    /** 订单图表：按月趋势 + 按货号排行（金额＝总计 RUB 净额），一次查询保证口径一致。 */
    OzonAccrualChartVo accrualChart(Long scopeShopId, String serviceGroup, String month);

    /** 订单图表下钻：某个卖家货号或某个月份下的订单费用原始明细行，后端分页返回。 */
    PageResult<OzonAccrualReportVo> accrualChartRows(Long scopeShopId, String serviceGroup, String sku, String month, PageQuery page);

    /**
     * 汇总图表：把交货 / 订单 / 退货三个主题的月度与货号汇总并到一起，一次查询保证口径一致。
     * 三个主题单位统一为「件」（订单数量 = 同一个应计费用编号只计一次）。
     * 「按月趋势」不受月份筛选影响，「按货号排行」受月份筛选影响；下钻明细复用各主题已有的下钻接口。
     */
    OzonSummaryChartVo summaryChart(Long scopeShopId, String month);
}
