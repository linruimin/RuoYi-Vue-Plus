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
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

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
    private static final Set<String> SUPPLY_SORT = Set.of("id", "orderId", "applicationNo", "deliveryType",
        "status", "shipmentDate", "shipmentTime", "storageCluster", "dispatchPoint", "completionDate",
        "deliveryId", "productName", "localProductName", "itemCode", "sku", "liquidity", "quantity", "volume");
    private static final Set<String> RETURNS_SORT = Set.of("reportMonth", "shopId", "shopName", "articleNo", "sku",
        "localProductName", "ozonProductName", "shipmentCount", "returnQty", "processedCount", "pendingCount",
        "disposalCount", "soldUnits", "returnRate", "storageFeeRub", "disposalFeeRub", "maxPriceRub",
        "avgStorageDays", "firstReturnDate", "lastReturnDate");

    @Override
    public PageResult<OzonMonthlyReportVo> monthly(OzonReportQuery q, PageQuery input) {
        var w = new QueryWrapper<OzonMonthlyReport>();
        if (StringUtils.isNotBlank(q.getSellerSku())) w.eq("seller_sku", q.getSellerSku());
        if (StringUtils.isNotBlank(q.getOzonSku())) w.eq("ozon_sku", q.getOzonSku());
        if (StringUtils.isNotBlank(q.getProductName())) w.like("product_name", q.getProductName());
        if (q.getReportMonth()!=null) w.eq("report_month",q.getReportMonth().withDayOfMonth(1));
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
        Page<OzonAccrualSummaryVo> result = accrualMapper.selectSummary(page, q);
        fillProductNames(result.getRecords());
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
    public List<OzonSupplyStatsVo> supplyStats(Long scopeShopId, String status) {
        // 空状态表示不限申请状态；店铺为空表示不限店铺。
        return supplyMapper.selectProductStats(scopeShopId, status);
    }

    @Override
    public List<OzonSupplyReportVo> supplyProductRows(Long scopeShopId, String status, String sku) {
        // 与统计口径保持一致：空状态不限申请状态，店铺为空不限店铺。
        return supplyMapper.selectProductRows(scopeShopId, status, sku);
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

    /** 仅对当前页批量查商品名，避免GROUP_CONCAT长度限制截断多商品名称；按店铺 + 费用编号归集。 */
    private void fillProductNames(List<OzonAccrualSummaryVo> rows) {
        if (rows.isEmpty()) return;
        var ids = rows.stream().map(OzonAccrualSummaryVo::getAccrualId).toList();
        var query = QueryBuilder.lambda(OzonAccrualReport.class).build();
        query.select(OzonAccrualReport::getShopId, OzonAccrualReport::getAccrualId, OzonAccrualReport::getProductName)
            .in(OzonAccrualReport::getAccrualId, ids)
            .isNotNull(OzonAccrualReport::getProductName)
            .groupBy(OzonAccrualReport::getShopId, OzonAccrualReport::getAccrualId, OzonAccrualReport::getProductName)
            .orderByAsc(OzonAccrualReport::getProductName);
        Map<String, Set<String>> names = new HashMap<>();
        for (OzonAccrualReport row : accrualMapper.selectList(query)) {
            if (StringUtils.isNotBlank(row.getProductName())) {
                names.computeIfAbsent(shopKey(row.getShopId(), row.getAccrualId()), key -> new LinkedHashSet<>()).add(row.getProductName().trim());
            }
        }
        for (OzonAccrualSummaryVo row : rows) {
            row.setProductName(String.join(" / ", names.getOrDefault(shopKey(row.getShopId(), row.getAccrualId()), Set.of())));
        }
    }

    /** 用店铺 + 费用编号定位一组应计记录。 */
    private static String shopKey(Long shopId, String accrualId) {
        return shopId + ":" + accrualId;
    }

    /** 明细为只读实体查询，店铺名按 shop 表回填。 */
    private void fillShopNames(List<OzonAccrualReportVo> rows) {
        if (rows.isEmpty()) return;
        Map<Long, String> names = new HashMap<>();
        for (OzonBizShop shop : shopMapper.selectList(new QueryWrapper<>())) {
            names.put(shop.getId(), shop.getName());
        }
        for (OzonAccrualReportVo row : rows) {
            row.setShopName(names.get(row.getShopId()));
        }
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
}
