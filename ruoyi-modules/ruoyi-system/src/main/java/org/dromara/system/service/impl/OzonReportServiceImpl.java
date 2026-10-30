package org.dromara.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.query.QueryBuilder;
import org.dromara.system.domain.*;
import org.dromara.system.domain.bo.OzonReportQuery;
import org.dromara.system.domain.vo.*;
import org.dromara.system.mapper.*;
import org.dromara.system.service.IOzonReportService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** 查询宿主 MySQL 的现有报表，不复制或修改源数据。 */
@DS("ozon")
@RequiredArgsConstructor
@Service
public class OzonReportServiceImpl implements IOzonReportService {
    private final OzonMonthlyReportMapper monthlyMapper;
    private final OzonAccrualReportMapper accrualMapper;
    private final OzonSupplyReportMapper supplyMapper;
    private final OzonReturnsReportMapper returnsReportMapper;
    private final OzonBizShopMapper shopMapper;

    private static final Set<String> MONTHLY_SORT = Set.of("rowId", "reportMonth", "sellerSku", "ozonSku",
        "productName", "soldUnits", "salesRecordCount", "positiveAccrualTotalRub", "salesRevenueRub",
        "discountPointsRub", "partnerProgramsRub", "otherAccrualsRub", "netSalesIncomeRub", "taxRub",
        "finalTakeHomeRub", "finalTakeHomeCny", "importedAt", "recordName", "localProductName", "unitTakeHomeCny", "averageCost", "unitMultiple", "totalCost", "totalProfit");
    private static final Set<String> SUMMARY_SORT = Set.of("rowId", "accrualId", "shopId", "shopName",
        "firstAccrualDate", "accrualDate", "positiveAmountRub", "negativeAmountRub", "totalAmountRub",
        "recordCount", "productName", "afterTaxAmountRub", "totalAmountCny");
    private static final Set<String> LINE_SORT = Set.of("rowId", "accrualId", "shopId", "shopName",
        "accrualDate", "serviceGroup",
        "accrualType", "sellerSku", "ozonSku", "productName", "quantity", "sellerPriceRub",
        "orderAcceptedOrServiceDate", "salesPlatform", "fulfillmentScheme", "ozonCommissionPct",
        "localizationIndexPct", "averageDeliveryTimeHours", "totalAmountRub", "importedAt");
    /**
     * 「1.1.订单图表」下钻（一行 = 一个应计项目）的排序白名单。
     * 与 LINE_SORT 不同：下钻读的是 ozon_accrual_item，没有费用分组/应计类型/数量这些列。
     */
    private static final Set<String> ITEM_SORT = Set.of("rowId", "accrualId", "shopId", "shopName",
        "statMonth", "sellerSku", "productName", "accrualDate", "firstAccrualDate", "recordCount",
        "positiveAmountRub", "negativeAmountRub", "totalAmountRub", "afterTaxAmountRub",
        "totalAmountCny");
    private static final Set<String> SUPPLY_SORT = Set.of("id", "orderId", "applicationNo", "deliveryType",
        "status", "shipmentDate", "shipmentTime", "storageCluster", "dispatchPoint", "completionDate",
        "deliveryId", "productName", "localProductName", "itemCode", "sku", "liquidity", "quantity", "volume");
    private static final Set<String> RETURNS_SORT = Set.of("reportMonth", "shopId", "shopName", "articleNo", "sku",
        "localProductName", "ozonProductName", "shipmentCount", "returnQty", "processedCount", "pendingCount",
        "disposalCount", "soldUnits", "returnRate", "storageFeeRub", "disposalFeeRub", "maxPriceRub",
        "avgStorageDays", "firstReturnDate", "lastReturnDate");

    /**
     * 「未标注货号」哨兵值。
     * 订单图表的货号槽位里有一个是 seller_sku 为空的行（平台级费用：广告点击、FBO 跨仓中转、仓储费、
     * 债权债务抵销等，源数据本身就没填货号）。点击它下钻时前端不能把空串当参数下发：
     * 若依的 tansParams 对 {@code value === ''} 会整条跳过，空串根本进不了 URL，后端收到 null
     * 就当成「不过滤」，会把整库明细都返回（曾因此让弹窗显示 381,261 行而不是 780 行）。
     * 所以约定用这个非空哨兵显式表达「只看没有卖家货号的明细」，进 Service 后翻译回空串。
     */
    private static final String NO_SKU_TOKEN = "__NO_SKU__";

    /**
     * 汇总图表（0.0）交货侧固定口径：只统计「已完成」的交货申请。
     * 交货明细里有已完成 / 已取消 / 已逾期 / 已准备发运 / 在发运点 / 输入数据六种状态，
     * 后四种（以及已取消）并不代表真正发出去货，混在一起会让汇总数虚高
     * （全量 88,835 件 vs 已完成 54,974 件）。这里与「0.1.交货图表」的默认状态保持一致。
     */
    private static final String SUPPLY_DONE_STATUS = "已完成";

    @Override
    public PageResult<OzonMonthlyReportVo> monthly(OzonReportQuery q, PageQuery input) {
        var w = new QueryWrapper<OzonMonthlyReport>();
        if (StringUtils.isNotBlank(q.getSellerSku())) w.eq("seller_sku", q.getSellerSku());
        if (StringUtils.isNotBlank(q.getOzonSku())) w.eq("ozon_sku", q.getOzonSku());
        if (StringUtils.isNotBlank(q.getProductName())) w.like("product_name", q.getProductName());
        if (q.getReportMonth()!=null) w.eq("report_month",q.getReportMonth().withDayOfMonth(1));
        // 全局店铺范围（首页选的店铺）：「全部」时前端不下发，这里也就不加条件。
        // 月报表自 2026-10-10 起带 shop_id（ozon_product_monthly_summary），在此之前的数据已重跑补齐。
        if (q.getScopeShopId() != null) w.eq("shop_id", q.getScopeShopId());
        // 「未标注货号」合成行（seller_sku='' AND ozon_sku=0）只给订单图表承载金额，
        // 不是产品销售行 —— 列表必须排除（判据与 mysql/12_*.sql 生成合成行时一致）。
        w.apply("NOT (seller_sku = '' AND ozon_sku = 0)");
        boolean sku = "sku".equals(q.getGroupBy());
        String group = sku ? "sellerSku" : "reportMonth";
        String sort = sortField(input, MONTHLY_SORT, sku ? "reportMonth" : "finalTakeHomeRub");
        Page<OzonMonthlyReportVo> page = boundedPage(input);
        // 分组键始终优先，使跨页排序也保持同组相邻。
        boolean grouped = !"none".equals(q.getGroupBy());
        if (grouped) page.addOrder(order(group, q.getGroupDesc()!=null ? !q.getGroupDesc() : group.equals(sort) ? ascending(input) : sku));
        if (!grouped || !group.equals(sort)) page.addOrder(order(sort, ascending(input)));
        page.addOrder(OrderItem.desc("row_id"));
        Page<OzonMonthlyReportVo> result = monthlyMapper.selectViewPage(page, w);
        fillMonthlyShopNames(result.getRecords());
        return PageResult.build(result.getRecords(), result.getTotal());
    }

    @Override
    public PageResult<OzonAccrualSummaryVo> accruals(OzonReportQuery q, PageQuery input) {
        validateDates(q);
        Page<OzonAccrualSummaryVo> page = boundedPage(input);
        // 店铺名与店铺一一对应，统一按 shop_id 排序，避免按未分组的名称排序。
        String summarySort = sortField(input, SUMMARY_SORT, "accrualDate");
        if ("shopName".equals(summarySort)) summarySort = "shopId";
        page.addOrder(order(summarySort, ascending(input)));
        page.addOrder(OrderItem.desc("row_id"));
        // 读的是**蒸馏表** ozon_accrual_summary（一行 = 一条应计费用明细 = 店铺×应计费用编号，
        // 由 scripts/ozon_accruals_daily_sync.py 每次入库后重建）。原先每次都拿 ozon_accruals
        // 38.6 万行做 GROUP BY + 一次额外的商品名补查，翻页 ≈0.9s；现在是一次索引扫描。
        Page<OzonAccrualSummaryVo> result = accrualMapper.selectSummary(page, q);
        return PageResult.build(result.getRecords(), result.getTotal());
    }

    @Override
    public List<OzonProductSalesTrendVo> salesTrend(OzonReportQuery q) {
        validateDates(q);
        return accrualMapper.selectSalesTrend(q);
    }

    @Override
    public PageResult<OzonAccrualReportVo> accrualLines(OzonReportQuery q, PageQuery input) {
        if (StringUtils.isBlank(q.getAccrualId()) && q.getRowId() == null) {
            throw new ServiceException("请选择一个应计费用编号");
        }
        var w = QueryBuilder.lambda(OzonAccrualReport.class).build();
        if (StringUtils.isNotBlank(q.getAccrualId())) {
            w.eq(OzonAccrualReport::getAccrualId, q.getAccrualId());
        } else {
            w.eq(OzonAccrualReport::getRowId, q.getRowId());
        }
        if (q.getScopeShopId() != null) {
            w.eq(OzonAccrualReport::getShopId, q.getScopeShopId());
        }
        Page<OzonAccrualReport> page = boundedPage(input);
        String lineSort = sortField(input, LINE_SORT, "accrualDate");
        if ("shopName".equals(lineSort)) lineSort = "shopId";
        page.addOrder(order(lineSort, ascending(input)));
        page.addOrder(OrderItem.desc("row_id"));
        Page<OzonAccrualReportVo> result = accrualMapper.selectVoPage(page, w);
        fillShopNames(result.getRecords());
        return PageResult.build(result.getRecords(), result.getTotal());
    }

    @Override
    public PageResult<OzonSupplyReportVo> supply(OzonReportQuery q, PageQuery input) {
        var w = QueryBuilder.lambda(OzonSupplyReport.class)
            .eqIfText(OzonSupplyReport::getItemCode, q.getSellerSku())
            .eqIfText(OzonSupplyReport::getSku, q.getOzonSku())
            .likeIfText(OzonSupplyReport::getProductName, q.getProductName())
            .eqIfText(OzonSupplyReport::getApplicationNo, q.getApplicationNo()).build();
        // 状态为空（默认）时不限申请状态，即显示全部；“申请状态”筛选选中具体状态才过滤。
        if (StringUtils.isNotBlank(q.getStatus())) w.eq(OzonSupplyReport::getStatus, q.getStatus());
        // 全局店铺范围（首页选的店铺）：「全部」时前端不下发，这里也就不加条件。
        // ⚠️ 列表页原先漏了这段 —— 图表与下钻都按店铺过滤，只有列表没有，
        // 表现就是「切换店铺后 0.交货申请明细 的数据完全不变」（2026-10-10 用户报）。
        // ⚠️ 这里必须用 apply 而不是 eq("shop_id", x)：w 是 LambdaQueryWrapper，eq 只接受 SFunction，
        //    字符串列名编译不过；apply 的 {0} 仍是参数占位，无注入风险。
        if (q.getScopeShopId() != null) w.apply("shop_id = {0}", q.getScopeShopId());
        String sort = sortField(input, SUPPLY_SORT, "completionDate");
        Page<OzonSupplyReport> page = boundedPage(input);
        String expression = StringUtils.toUnderScoreCase(sort);
        if ("completionDate".equals(sort) || "shipmentDate".equals(sort)) expression = "STR_TO_DATE(" + expression + ", '%d.%m.%Y')";
        String grouping = "sku".equals(q.getGroupBy()) ? "sku " + (Boolean.TRUE.equals(q.getGroupDesc()) ? "DESC, " : "ASC, ") : "";
        // 字段由sortField白名单映射，表达式与方向均为服务端固定值。
        w.last("ORDER BY " + grouping + expression + (ascending(input) ? " ASC" : " DESC") + ", id DESC");
        Page<OzonSupplyReportVo> result = supplyMapper.selectViewPage(page, w);
        return PageResult.build(result.getRecords(), result.getTotal());
    }

    @Override
    public OzonSupplyChartVo supplyChart(Long scopeShopId, String status, String month) {
        // 空状态不限申请状态、空月份不限月份、店铺为空不限店铺；两个维度共用同一筛选，保证口径一致。
        // 「按月趋势」始终展示全部月份（不受 month 影响），「按产品排行」受 month 筛选。
        OzonSupplyChartVo chart = new OzonSupplyChartVo();
        chart.setMonths(supplyMapper.selectMonthStats(scopeShopId, status));
        chart.setProducts(supplyMapper.selectProductStats(scopeShopId, status, month));
        return chart;
    }

    @Override
    public List<OzonSupplyReportVo> supplyProductRows(Long scopeShopId, String status, String sku, String month) {
        // 与图表口径保持一致：下钻行同样受店铺 / 状态 / 月份过滤。
        return supplyMapper.selectProductRows(scopeShopId, status, sku, month);
    }

    @Override
    public OzonReturnsChartVo returnsChart(Long scopeShopId, String status, String month) {
        // 空状态不限退货状态、空月份不限月份、店铺为空不限店铺；两个维度共用同一筛选，保证口径一致。
        OzonReturnsChartVo chart = new OzonReturnsChartVo();
        chart.setMonths(returnsReportMapper.selectMonthStats(scopeShopId, status));
        chart.setProducts(returnsReportMapper.selectProductStats(scopeShopId, status, month));
        return chart;
    }

    @Override
    public List<OzonBizReturnsVo> returnsChartRows(Long scopeShopId, String status, String month, String articleNo) {
        // 与图表口径保持一致：下钻行同样受店铺 / 状态 / 月份过滤。
        return returnsReportMapper.selectChartRows(scopeShopId, status, month, articleNo);
    }

    @Override
    public OzonAccrualChartVo accrualChart(Long scopeShopId, String month) {
        // 图表读**产品月报**而不是源表 ozon_accruals（38.6 万行按表达式分组，接口实测 9,059ms）：
        // 「按 (店铺, 月, 货号) 的净额」与月报的 net_sales_income_rub 逐格一致，两者本来就是同样的归月口径。
        // 原先还有一个「费用分组」筛选（`ozon_accruals.service_group`），它会把「一笔销售的费用」拆散，
        // 用处不大且无法由月报承载，已整体去掉。
        // 「按月趋势」始终展示全部月份（不受 month 影响），「按货号排行」受 month 筛选。
        OzonAccrualChartVo chart = new OzonAccrualChartVo();
        chart.setMonths(monthlyMapper.selectChartMonthStats(scopeShopId));
        chart.setProducts(monthlyMapper.selectChartProductStats(scopeShopId, month));
        return chart;
    }

    @Override
    public PageResult<OzonAccrualSummaryVo> accrualChartRows(Long scopeShopId, String sku,
                                                             String month, PageQuery input) {
        // 与图表口径保持一致：下钻同样受店铺 / 月份 / 货号过滤。
        // 数据源是下钻预聚合表 ozon_accrual_item（一行 = 一个应计项目在该月该货号上的费用），
        // 粒度与图表柱子完全相同，所以弹窗的「净额合计」严格等于柱高。
        // 前端「未标注货号」下发的是哨兵值（空串会被 tansParams 丢弃），这里翻译成空串再进 SQL。
        String skuFilter = NO_SKU_TOKEN.equals(sku) ? "" : sku;
        Page<OzonAccrualSummaryVo> page = boundedPage(input);
        String rowSort = sortField(input, ITEM_SORT, "accrualDate");
        if ("shopName".equals(rowSort)) rowSort = "shopId";
        page.addOrder(order(rowSort, ascending(input)));
        page.addOrder(OrderItem.desc("row_id"));
        Page<OzonAccrualSummaryVo> result = accrualMapper.selectChartRows(page, scopeShopId, skuFilter, month);
        fillSummaryShopNames(result.getRecords());
        return PageResult.build(result.getRecords(), result.getTotal());
    }

    @Override
    public OzonSummaryChartVo summaryChart(Long scopeShopId, String month) {
        // 汇总页把三个主题的汇总并在一起，口径与各自的图表页保持一致：
        // 三个主题各自「按月趋势」不带月份筛选（看全貌），「按货号排行」带月份筛选。
        // 汇总页不提供各主题的原生筛选（交货状态 / 退货状态）。其中交货侧固定按「已完成」统计
        // （与「0.1.交货图表」的默认状态一致，见 SUPPLY_DONE_STATUS），退货按全量口径。
        // 订单侧展示「售出件数」而不是应计金额，数据源是产品月报的 sold_units：应计明细是按「费用」铺开的
        // （一笔销售横跨「销售收入 / 佣金 / 折扣积分 / 物流…」七八行，还混着大量与卖货无关的服务费单据），
        // 无论怎么按编号去重都不等于「卖出多少件」；产品月报的 sold_units 才是真实售出件数，
        // 与交货 / 退货同为「件」，三个指标这才真正可比。

        // ---- 按月趋势：三个主题各自聚合后按月份取并集，月份升序 ----
        Map<String, OzonSummaryChartVo.MonthStat> monthMap = new TreeMap<>();
        for (OzonSupplyChartVo.MonthStat s : supplyMapper.selectMonthStats(scopeShopId, SUPPLY_DONE_STATUS)) {
            OzonSummaryChartVo.MonthStat m = monthMap.computeIfAbsent(s.getMonth(), OzonReportServiceImpl::summaryMonth);
            m.setSupplyQty(s.getTotalQuantity());
            m.setSupplyOrders(s.getOrderCount());
        }
        for (OzonAccrualChartVo.MonthStat a : monthlyMapper.selectSoldUnitsMonths(scopeShopId)) {
            OzonSummaryChartVo.MonthStat m = monthMap.computeIfAbsent(a.getMonth(), OzonReportServiceImpl::summaryMonth);
            m.setAccrualQty(a.getTotalQuantity());
            m.setAccrualCount(a.getAccrualCount());
        }
        for (OzonReturnsChartVo.MonthStat r : returnsReportMapper.selectMonthStats(scopeShopId, null)) {
            OzonSummaryChartVo.MonthStat m = monthMap.computeIfAbsent(r.getMonth(), OzonReportServiceImpl::summaryMonth);
            m.setReturnQty(r.getReturnQty());
            m.setReturnShipments(r.getShipmentCount());
        }
        OzonSummaryChartVo chart = new OzonSummaryChartVo();
        // 三个指标都是空 / 0 的月份没有可展示的内容，留着只会变成一根都没有的空轴位。
        // 典型例子：2026-04 只有「已取消」的交货，交货改成只算已完成后该月三个指标全为 0 —— 直接去掉。
        List<OzonSummaryChartVo.MonthStat> monthList = monthMap.values().stream()
            .filter(OzonReportServiceImpl::summaryHasData)
            .collect(Collectors.toCollection(ArrayList::new));
        chart.setMonths(monthList);

        // ---- 按货号排行：三个主题各自聚合后按卖家货号归并（交货 sku / 订单 seller_sku / 退货 article_no）----
        Map<String, OzonSummaryChartVo.ProductStat> productMap = new LinkedHashMap<>();
        for (OzonSupplyStatsVo s : supplyMapper.selectProductStats(scopeShopId, SUPPLY_DONE_STATUS, month)) {
            OzonSummaryChartVo.ProductStat p = productMap.computeIfAbsent(summaryKey(s.getSku()),
                OzonReportServiceImpl::summaryProduct);
            p.setSupplyQty(s.getTotalQuantity());
            p.setSupplyOrders(s.getOrderCount());
            fillSummaryMeta(p, s.getLocalProductName(), s.getAttachmentJson());
        }
        for (OzonAccrualChartVo.ProductStat a : monthlyMapper.selectSoldUnitsProducts(scopeShopId, month)) {
            OzonSummaryChartVo.ProductStat p = productMap.computeIfAbsent(summaryKey(a.getSku()),
                OzonReportServiceImpl::summaryProduct);
            p.setAccrualQty(a.getTotalQuantity());
            p.setAccrualCount(a.getAccrualCount());
            fillSummaryMeta(p, StringUtils.isNotBlank(a.getProductName()) ? a.getProductName() : a.getOzonProductName(),
                a.getAttachmentJson());
        }
        for (OzonReturnsChartVo.ProductStat r : returnsReportMapper.selectProductStats(scopeShopId, null, month)) {
            OzonSummaryChartVo.ProductStat p = productMap.computeIfAbsent(summaryKey(r.getArticleNo()),
                OzonReportServiceImpl::summaryProduct);
            p.setReturnQty(r.getReturnQty());
            p.setReturnShipments(r.getShipmentCount());
            fillSummaryMeta(p, r.getLocalProductName(), r.getAttachmentJson());
        }
        // 同月份：三个指标全为空 / 0 的货号（例如只有「未完成交货」或只有平台级零数量费用）不占位。
        List<OzonSummaryChartVo.ProductStat> products = productMap.values().stream()
            .filter(OzonReportServiceImpl::summaryHasData)
            .collect(Collectors.toCollection(ArrayList::new));
        // 默认按交货件数倒序，其次订单数量、退货件数（与页面上「交货 / 订单 / 退货」的展示顺序一致），
        // 最后货号升序兜底，保证顺序稳定可复现。
        Comparator<OzonSummaryChartVo.ProductStat> bySupply =
            Comparator.comparingLong((OzonSummaryChartVo.ProductStat p) -> summaryQty(p.getSupplyQty())).reversed();
        Comparator<OzonSummaryChartVo.ProductStat> byAccrual =
            Comparator.comparingLong((OzonSummaryChartVo.ProductStat p) -> summaryQty(p.getAccrualQty())).reversed();
        Comparator<OzonSummaryChartVo.ProductStat> byReturn =
            Comparator.comparingLong((OzonSummaryChartVo.ProductStat p) -> summaryQty(p.getReturnQty())).reversed();
        products.sort(bySupply.thenComparing(byAccrual).thenComparing(byReturn)
            .thenComparing(p -> p.getSku() == null ? "" : p.getSku()));
        chart.setProducts(products);
        return chart;
    }

    /** 汇总图表的月份槽位（月份取三个主题的并集，缺的指标留空）。 */
    private static OzonSummaryChartVo.MonthStat summaryMonth(String month) {
        OzonSummaryChartVo.MonthStat stat = new OzonSummaryChartVo.MonthStat();
        stat.setMonth(month);
        return stat;
    }

    /** 汇总图表的货号槽位；无货号（订单侧平台级费用）归一成空串，与图表里的「未标注货号」一致。 */
    private static OzonSummaryChartVo.ProductStat summaryProduct(String sku) {
        OzonSummaryChartVo.ProductStat stat = new OzonSummaryChartVo.ProductStat();
        stat.setSku(sku);
        return stat;
    }

    private static String summaryKey(String sku) {
        return sku == null ? "" : sku.trim();
    }

    private static long summaryQty(Integer value) {
        return value == null ? 0L : value.longValue();
    }

    /** 月份槽位是否有内容：三个指标至少有一个不为空且不等于 0。 */
    private static boolean summaryHasData(OzonSummaryChartVo.MonthStat m) {
        return summaryQty(m.getSupplyQty()) + summaryQty(m.getAccrualQty()) + summaryQty(m.getReturnQty()) > 0;
    }

    /** 货号槽位是否有内容：三个指标至少有一个不为空且不等于 0。 */
    private static boolean summaryHasData(OzonSummaryChartVo.ProductStat p) {
        return summaryQty(p.getSupplyQty()) + summaryQty(p.getAccrualQty()) + summaryQty(p.getReturnQty()) > 0;
    }

    /** 品名与图片三个主题谁先取到用谁，避免后面的空值把已有值覆盖掉。 */
    private static void fillSummaryMeta(OzonSummaryChartVo.ProductStat target, String productName, String attachmentJson) {
        if (StringUtils.isBlank(target.getProductName()) && StringUtils.isNotBlank(productName)) {
            target.setProductName(productName);
        }
        if (StringUtils.isBlank(target.getAttachmentJson()) && StringUtils.isNotBlank(attachmentJson)) {
            target.setAttachmentJson(attachmentJson);
        }
    }

    @Override
    public PageResult<OzonReturnsReportVo> returnsReport(OzonReportQuery q, PageQuery input) {
        var w = new QueryWrapper<OzonBizReturns>();
        // 外层同时挂了 r（汇总子查询）与 p_view（product），article_no / sku / shop_id 都重名，
        // 必须用限定列名，否则报 "Column 'xxx' in where clause is ambiguous"。
        if (StringUtils.isNotBlank(q.getSellerSku())) w.apply("r.article_no = {0}", q.getSellerSku());
        if (StringUtils.isNotBlank(q.getOzonSku())) w.apply("r.sku = {0}", q.getOzonSku());
        if (StringUtils.isNotBlank(q.getProductName())) w.apply("r.ozon_product_name LIKE CONCAT('%', {0}, '%')", q.getProductName());
        if (q.getReportMonth() != null) w.apply("r.report_month = {0}", q.getReportMonth().withDayOfMonth(1).toString());
        if (q.getScopeShopId() != null) w.apply("r.shop_id = {0}", q.getScopeShopId());
        boolean sku = "sku".equals(q.getGroupBy());
        String group = sku ? "sku" : "report_month";
        String sort = sortField(input, RETURNS_SORT, "returnQty");
        Page<OzonReturnsReportVo> page = boundedPage(input);
        // 分组键始终优先，使跨页排序也保持同组相邻。
        boolean grouped = !"none".equals(q.getGroupBy());
        if (grouped) page.addOrder(order(group, q.getGroupDesc() != null ? !q.getGroupDesc() : group.equals(sort) ? ascending(input) : sku));
        if (!grouped || !group.equals(sort)) page.addOrder(order(sort, ascending(input)));
        page.addOrder(OrderItem.desc("report_month"));
        Page<OzonReturnsReportVo> result = returnsReportMapper.selectReportPage(page, w);
        return PageResult.build(result.getRecords(), result.getTotal());
    }

    /** 明细为只读实体查询，店铺名按 shop 表回填。 */
    private void fillShopNames(List<OzonAccrualReportVo> rows) {
        if (rows.isEmpty()) return;
        Map<Long, String> names = shopNames();
        for (OzonAccrualReportVo row : rows) {
            row.setShopName(names.get(row.getShopId()));
        }
    }

    /** 产品月报的店铺名（「全部店铺」时列表里要能分辨每行属于哪个店）。 */
    private void fillMonthlyShopNames(List<OzonMonthlyReportVo> rows) {
        if (rows.isEmpty()) return;
        Map<Long, String> names = shopNames();
        for (OzonMonthlyReportVo row : rows) {
            row.setShopName(names.get(row.getShopId()));
        }
    }

    /** 订单图表下钻（应计项目）的店铺名。 */
    private void fillSummaryShopNames(List<OzonAccrualSummaryVo> rows) {
        if (rows.isEmpty()) return;
        Map<Long, String> names = shopNames();
        for (OzonAccrualSummaryVo row : rows) {
            row.setShopName(names.get(row.getShopId()));
        }
    }

    private Map<Long, String> shopNames() {
        Map<Long, String> names = new HashMap<>();
        for (OzonBizShop shop : shopMapper.selectList(new QueryWrapper<>())) {
            names.put(shop.getId(), shop.getName());
        }
        return names;
    }

    @Override
    public List<OzonSalesProductVo> salesProducts(OzonReportQuery q) {
        validateDates(q);
        return accrualMapper.selectSalesProducts(q);
    }

    @Override
    public List<OzonProductSaleVo> salesTransactions(OzonReportQuery q) {
        validateDates(q);
        if (StringUtils.isBlank(q.getProductKey())) {
            throw new ServiceException("请选择要查看销售趋势的产品");
        }
        return accrualMapper.selectSalesTransactions(q);
    }

    private void validateDates(OzonReportQuery q) {
        if (q.getStartDate() != null && q.getEndDate() != null && q.getStartDate().isAfter(q.getEndDate())) {
            throw new ServiceException("开始日期不能晚于结束日期");
        }
    }

    /** 只允许当前视图明确支持的字段，不能传入SQL表达式。 */
    private String sortField(PageQuery input, Set<String> allowed, String fallback) {
        String field = StringUtils.isBlank(input.getOrderByColumn()) ? fallback : input.getOrderByColumn();
        if (!allowed.contains(field)) throw new ServiceException("不支持的排序字段");
        return field;
    }

    private boolean ascending(PageQuery input) {
        String direction = input.getIsAsc();
        if (StringUtils.isBlank(direction) || "desc".equals(direction) || "descending".equals(direction)) return false;
        if ("asc".equals(direction) || "ascending".equals(direction)) return true;
        throw new ServiceException("排序方向有误");
    }

    private OrderItem order(String field, boolean asc) {
        String column = StringUtils.toUnderScoreCase(field);
        return asc ? OrderItem.asc(column) : OrderItem.desc(column);
    }

    private <T> Page<T> boundedPage(PageQuery input) {
        PageQuery safe = new PageQuery();
        safe.setPageNum(input.getPageNum() == null ? 1 : Math.max(1, input.getPageNum()));
        safe.setPageSize(input.getPageSize() == null ? 20 : Math.max(1, Math.min(100, input.getPageSize())));
        return safe.build();
    }

    // ==================== 2.2 退货成本 ====================

    /** 应计里的退货成本科目（金额为负，展示时取绝对值）。 */
    private static final String COST_REVERSE_LOGISTICS = "逆向物流";
    private static final String COST_HANDLING = "合作伙伴处理退货、取消和拒收";
    private static final String COST_PICKUP = "PickUpPointReturnAcceptance";
    private static final String COST_REVENUE_REFUND = "收入退回";
    private static final String COST_COMMISSION_REFUND = "佣金返还";

    @Override
    public OzonReturnsCostVo returnsCost(Long scopeShopId, String status, String month) {
        // 退货侧是「未完结退货快照」，应计侧是历史累计，两者没有货件号可勾稽：
        // 只能按「店铺 + 卖家货号 + 订单日期」把应计金额均摊到该组的退货单上。
        // 订单日期为空的应计行无法归属，单独计入「未分摊」，不计入单均成本。
        Map<String, Object> returnSummary = returnsReportMapper.selectCostReturnSummary(scopeShopId, status, month);
        List<Map<String, Object>> accrualTotals = returnsReportMapper.selectCostAccrualTotals(scopeShopId, month);
        List<Map<String, Object>> accrualGroups = returnsReportMapper.selectCostAccrualGroups(scopeShopId, month);
        List<Map<String, Object>> returnGroups = returnsReportMapper.selectCostReturnGroups(scopeShopId, status, month);
        List<OzonReturnsCostVo.CostRow> rows = returnsReportMapper.selectCostRows(scopeShopId, status, month);

        Map<String, Integer> groupCounts = new HashMap<>();
        for (Map<String, Object> group : returnGroups) {
            groupCounts.put(costKey(group.get("shopId"), group.get("articleNo"), group.get("orderDate")),
                costInt(group.get("cnt")));
        }

        Map<String, Map<String, BigDecimal>> groupCosts = new HashMap<>();
        int unmappedCount = 0;
        for (Map<String, Object> group : accrualGroups) {
            BigDecimal amount = costDec(group.get("amount")).abs();
            String key = costKey(group.get("shopId"), group.get("articleNo"), group.get("orderDate"));
            Integer count = groupCounts.get(key);
            if (count == null || count <= 0) {
                unmappedCount += costInt(group.get("cnt"));
                continue;
            }
            groupCosts.computeIfAbsent(key, ignored -> new HashMap<>())
                .merge(String.valueOf(group.get("accrualType")), amount, BigDecimal::add);
        }

        // 应计总额（三项成本的真实出账金额）；分摊只决定「落到哪张单」，不改变总额。
        BigDecimal accrueReverse = BigDecimal.ZERO;
        BigDecimal accrueHandling = BigDecimal.ZERO;
        BigDecimal accruePickup = BigDecimal.ZERO;
        for (Map<String, Object> total : accrualTotals) {
            String type = String.valueOf(total.get("accrualType"));
            BigDecimal amount = costDec(total.get("amount")).abs();
            if (COST_REVERSE_LOGISTICS.equals(type)) accrueReverse = accrueReverse.add(amount);
            if (COST_HANDLING.equals(type)) accrueHandling = accrueHandling.add(amount);
            if (COST_PICKUP.equals(type)) accruePickup = accruePickup.add(amount);
        }

        for (OzonReturnsCostVo.CostRow row : rows) {
            String key = costKey(row.getShopId(), row.getArticleNo(), row.getOrderDate());
            Integer count = groupCounts.get(key);
            Map<String, BigDecimal> costs = groupCosts.get(key);
            BigDecimal reverse = BigDecimal.ZERO;
            BigDecimal handling = BigDecimal.ZERO;
            BigDecimal pickup = BigDecimal.ZERO;
            if (costs != null && count != null && count > 0) {
                reverse = costShare(costs.get(COST_REVERSE_LOGISTICS), count);
                handling = costShare(costs.get(COST_HANDLING), count);
                pickup = costShare(costs.get(COST_PICKUP), count);
            }
            row.setReverseLogisticsRub(reverse);
            row.setHandlingFeeRub(handling);
            row.setPickupFeeRub(pickup);
            row.setCostTotalRub(reverse.add(handling).add(pickup));
        }

        BigDecimal allocatedTotal = BigDecimal.ZERO;
        for (OzonReturnsCostVo.CostRow row : rows) {
            allocatedTotal = allocatedTotal.add(row.getCostTotalRub());
        }

        OzonReturnsCostVo.Summary summary = new OzonReturnsCostVo.Summary();
        summary.setShipmentCount(costInt(returnSummary.get("shipmentCount")));
        summary.setReturnQty(costInt(returnSummary.get("returnQty")));
        summary.setGoodsValueRub(costDec(returnSummary.get("goodsValueRub")).setScale(2, java.math.RoundingMode.HALF_UP));
        summary.setStorageFeeRub(costDec(returnSummary.get("storageFeeRub")).setScale(2, java.math.RoundingMode.HALF_UP));
        summary.setDisposalFeeRub(costDec(returnSummary.get("disposalFeeRub")).setScale(2, java.math.RoundingMode.HALF_UP));
        summary.setReverseLogisticsRub(accrueReverse.setScale(2, java.math.RoundingMode.HALF_UP));
        summary.setHandlingFeeRub(accrueHandling.setScale(2, java.math.RoundingMode.HALF_UP));
        summary.setPickupFeeRub(accruePickup.setScale(2, java.math.RoundingMode.HALF_UP));
        BigDecimal costTotal = accrueReverse.add(accrueHandling).add(accruePickup);
        summary.setCostTotalRub(costTotal.setScale(2, java.math.RoundingMode.HALF_UP));
        int shipments = summary.getShipmentCount() == null ? 0 : summary.getShipmentCount();
        summary.setCostPerShipmentRub(shipments > 0
            ? costTotal.divide(BigDecimal.valueOf(shipments), 2, java.math.RoundingMode.HALF_UP)
            : BigDecimal.ZERO);
        BigDecimal goodsValue = summary.getGoodsValueRub();
        summary.setCostRatePct(goodsValue != null && goodsValue.compareTo(BigDecimal.ZERO) > 0
            ? costTotal.multiply(BigDecimal.valueOf(100)).divide(goodsValue, 2, java.math.RoundingMode.HALF_UP)
            : BigDecimal.ZERO);
        BigDecimal revenueRefund = BigDecimal.ZERO;
        BigDecimal commissionRefund = BigDecimal.ZERO;
        for (Map<String, Object> total : accrualTotals) {
            String type = String.valueOf(total.get("accrualType"));
            BigDecimal amount = costDec(total.get("amount")).setScale(2, java.math.RoundingMode.HALF_UP);
            if (COST_REVENUE_REFUND.equals(type)) revenueRefund = revenueRefund.add(amount);
            if (COST_COMMISSION_REFUND.equals(type)) commissionRefund = commissionRefund.add(amount);
        }
        summary.setRevenueRefundRub(revenueRefund);
        summary.setCommissionRefundRub(commissionRefund);
        BigDecimal unmappedResidual = costTotal.subtract(allocatedTotal);
        summary.setUnmappedCount(unmappedCount);
        summary.setUnmappedCostRub(unmappedResidual.max(BigDecimal.ZERO).setScale(2, java.math.RoundingMode.HALF_UP));

        // 按卖家货号归并（明细成本合计 = 汇总成本，两者一定相等）
        Map<String, OzonReturnsCostVo.ArticleStat> articleMap = new LinkedHashMap<>();
        for (OzonReturnsCostVo.CostRow row : rows) {
            String key = row.getArticleNo() == null ? "" : row.getArticleNo();
            OzonReturnsCostVo.ArticleStat article = articleMap.get(key);
            if (article == null) {
                article = new OzonReturnsCostVo.ArticleStat();
                article.setArticleNo(row.getArticleNo());
                article.setProductName(row.getProductName());
                article.setImageUrl(row.getImageUrl());
                article.setShipmentCount(0);
                article.setReturnQty(0);
                article.setReverseLogisticsRub(BigDecimal.ZERO);
                article.setHandlingFeeRub(BigDecimal.ZERO);
                article.setPickupFeeRub(BigDecimal.ZERO);
                articleMap.put(key, article);
            }
            article.setShipmentCount(article.getShipmentCount() + 1);
            article.setReturnQty(article.getReturnQty() + costInt(row.getReturnQty()));
            article.setReverseLogisticsRub(article.getReverseLogisticsRub().add(row.getReverseLogisticsRub()));
            article.setHandlingFeeRub(article.getHandlingFeeRub().add(row.getHandlingFeeRub()));
            article.setPickupFeeRub(article.getPickupFeeRub().add(row.getPickupFeeRub()));
        }
        for (OzonReturnsCostVo.ArticleStat article : articleMap.values()) {
            BigDecimal total = article.getReverseLogisticsRub()
                .add(article.getHandlingFeeRub()).add(article.getPickupFeeRub());
            article.setCostTotalRub(total.setScale(2, java.math.RoundingMode.HALF_UP));
            article.setCostPerShipmentRub(article.getShipmentCount() > 0
                ? total.divide(BigDecimal.valueOf(article.getShipmentCount()), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
        }
        List<OzonReturnsCostVo.ArticleStat> articles = new ArrayList<>(articleMap.values());
        articles.sort((left, right) -> right.getCostTotalRub().compareTo(left.getCostTotalRub()));

        OzonReturnsCostVo result = new OzonReturnsCostVo();
        result.setSummary(summary);
        result.setArticles(articles);
        result.setRows(rows);
        return result;
    }

    /** 分摊键：店铺 + 卖家货号 + 订单日期。 */
    private static String costKey(Object shopId, Object articleNo, Object orderDate) {
        return String.valueOf(shopId) + '|' + (articleNo == null ? "" : articleNo) + '|'
            + (orderDate == null ? "" : orderDate);
    }

    /** 组内均摊：金额 / 组内退货单数，保留 2 位。 */
    private static BigDecimal costShare(BigDecimal amount, int count) {
        if (amount == null || count <= 0) return BigDecimal.ZERO;
        return amount.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);
    }

    private static BigDecimal costDec(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal decimal) return decimal;
        if (value instanceof Number number) return new BigDecimal(number.toString());
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return BigDecimal.ZERO;
        }
    }

    private static int costInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number number) return number.intValue();
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

}
