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
    /** 交货报表：按产品聚合交货数量，供图表展示。 */
    java.util.List<OzonSupplyStatsVo> supplyStats(Long scopeShopId, String status);
    /** 交货报表下钻：单个卖家货号下的交货申请明细行。 */
    java.util.List<OzonSupplyReportVo> supplyProductRows(Long scopeShopId, String status, String sku);

    /** 退货图表：按月趋势 + 按产品排行，一次查询保证口径一致。 */
    OzonReturnsChartVo returnsChart(Long scopeShopId, String status, String month);

    /** 退货图表下钻：某个卖家货号或某个月份下的退货明细行。 */
    java.util.List<OzonBizReturnsVo> returnsChartRows(Long scopeShopId, String status, String month, String articleNo);
}
