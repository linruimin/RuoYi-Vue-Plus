package org.dromara.system.controller.ozon;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.OzonReportQuery;
import org.dromara.system.domain.vo.*;
import org.dromara.system.service.IOzonReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** Ozon 订单报告查询接口。 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ozon/report")
public class OzonReportController extends BaseController {
    private final IOzonReportService reportService;

    /** 产品月度收入及到手金额。 */
    @SaCheckPermission("ozon:report:list")
    @GetMapping("/monthly/list")
    public R<PageResult<OzonMonthlyReportVo>> monthly(@Validated OzonReportQuery query, PageQuery page) {
        return R.ok(reportService.monthly(query, page));
    }

    /** 订单费用明细。 */
    @SaCheckPermission("ozon:report:list")
    @GetMapping("/accruals/list")
    public R<PageResult<OzonAccrualSummaryVo>> accruals(@Validated OzonReportQuery query, PageQuery page) {
        return R.ok(reportService.accruals(query, page));
    }

    /** 交货申请商品明细。 */
    @SaCheckPermission("ozon:report:list")
    @GetMapping("/supply/list")
    public R<PageResult<OzonSupplyReportVo>> supply(@Validated OzonReportQuery query, PageQuery page) {
        return R.ok(reportService.supply(query, page));
    }

    /** 交货报表：按产品汇总交货数量，供图表展示；状态与店铺为空表示不过滤。 */
    @SaCheckPermission("ozon:report:list")
    @GetMapping("/supply/stats")
    public R<java.util.List<OzonSupplyStatsVo>> supplyStats(@RequestParam(required = false) Long scopeShopId,
                                                            @RequestParam(required = false) String status) {
        return R.ok(reportService.supplyStats(scopeShopId, status));
    }

    /** 交货报表下钻：某个产品下的全部交货申请明细行，供点击柱子查看。 */
    @SaCheckPermission("ozon:report:list")
    @GetMapping("/supply/product-rows")
    public R<java.util.List<OzonSupplyReportVo>> supplyProductRows(@RequestParam(required = false) Long scopeShopId,
                                                                  @RequestParam(required = false) String status,
                                                                  @RequestParam String sku) {
        return R.ok(reportService.supplyProductRows(scopeShopId, status, sku));
    }

    /** 退货报表：按退货月份 × SKU 汇总退货件数、费用与退货率。 */
    @SaCheckPermission("ozon:report:list")
    @GetMapping("/returns-report/list")
    public R<PageResult<OzonReturnsReportVo>> returnsReport(@Validated OzonReportQuery query, PageQuery page) {
        return R.ok(reportService.returnsReport(query, page));
    }

    /** 按费用编号查看正负原始明细。 */
    @SaCheckPermission("ozon:report:list")
    @GetMapping("/accruals/lines")
    public R<PageResult<OzonAccrualReportVo>> accrualLines(@Validated OzonReportQuery query, PageQuery page) {
        return R.ok(reportService.accrualLines(query, page));
    }
    /** 原视图筛选范围内的产品销售趋势。 */
    @SaCheckPermission("ozon:report:list")
    @GetMapping("/accruals/sales-trend")
    public R<java.util.List<OzonProductSalesTrendVo>> salesTrend(@Validated OzonReportQuery query) {
        return R.ok(reportService.salesTrend(query));
    }
    /** 当前费用筛选结果中的产品选项。 */
    @SaCheckPermission("ozon:report:list")
    @GetMapping("/accruals/sales-products")
    public R<java.util.List<OzonSalesProductVo>> salesProducts(@Validated OzonReportQuery query) {
        return R.ok(reportService.salesProducts(query));
    }

    /** 按订单受理日期排列单产品的逐笔销售。 */
    @SaCheckPermission("ozon:report:list")
    @GetMapping("/accruals/sales-transactions")
    public R<java.util.List<OzonProductSaleVo>> salesTransactions(@Validated OzonReportQuery query) {
        return R.ok(reportService.salesTransactions(query));
    }
}

