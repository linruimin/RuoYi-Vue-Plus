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

    private static final Set<String> MONTHLY_SORT = Set.of("rowId", "reportMonth", "sellerSku", "ozonSku",
        "productName", "soldUnits", "salesRecordCount", "positiveAccrualTotalRub", "salesRevenueRub",
        "discountPointsRub", "partnerProgramsRub", "otherAccrualsRub", "netSalesIncomeRub", "taxRub",
        "finalTakeHomeRub", "finalTakeHomeCny", "importedAt", "recordName", "localProductName", "unitTakeHomeCny", "averageCost", "unitMultiple", "totalCost", "totalProfit");
    private static final Set<String> SUMMARY_SORT = Set.of("rowId", "accrualId", "firstAccrualDate",
        "accrualDate", "positiveAmountRub", "negativeAmountRub", "totalAmountRub", "recordCount",
        "productName", "afterTaxAmountRub", "totalAmountCny");
    private static final Set<String> LINE_SORT = Set.of("rowId", "accrualId", "accrualDate", "serviceGroup",
        "accrualType", "sellerSku", "ozonSku", "productName", "quantity", "sellerPriceRub",
        "orderAcceptedOrServiceDate", "salesPlatform", "fulfillmentScheme", "ozonCommissionPct",
        "localizationIndexPct", "averageDeliveryTimeHours", "totalAmountRub", "importedAt");
    private static final Set<String> SUPPLY_SORT = Set.of("id", "orderId", "applicationNo", "deliveryType",
        "status", "shipmentDate", "shipmentTime", "storageCluster", "dispatchPoint", "completionDate",
        "deliveryId", "productName", "itemCode", "sku", "liquidity", "quantity", "volume");

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
        page.addOrder(order(sortField(input, SUMMARY_SORT, "accrualDate"), ascending(input)));
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
        Page<OzonAccrualReport> page = boundedPage(input);
        page.addOrder(order(sortField(input, LINE_SORT, "accrualDate"), ascending(input)));
        page.addOrder(OrderItem.desc("row_id"));
        Page<OzonAccrualReportVo> result = accrualMapper.selectVoPage(page, w);
        return PageResult.build(result.getRecords(), result.getTotal());
    }

    @Override
    public PageResult<OzonSupplyReportVo> supply(OzonReportQuery q, PageQuery input) {
        var w = QueryBuilder.lambda(OzonSupplyReport.class)
            .eqIfText(OzonSupplyReport::getItemCode, q.getSellerSku())
            .eqIfText(OzonSupplyReport::getSku, q.getOzonSku())
            .likeIfText(OzonSupplyReport::getProductName, q.getProductName())
            .eqIfText(OzonSupplyReport::getApplicationNo, q.getApplicationNo()).build();
        // 原接口默认已完成；“全部”视图显式传空状态。
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

    /** 仅对当前页批量查商品名，避免GROUP_CONCAT长度限制截断多商品名称。 */
    private void fillProductNames(List<OzonAccrualSummaryVo> rows) {
        if (rows.isEmpty()) return;
        var ids = rows.stream().map(OzonAccrualSummaryVo::getAccrualId).toList();
        var query = QueryBuilder.lambda(OzonAccrualReport.class).build();
        query.select(OzonAccrualReport::getAccrualId, OzonAccrualReport::getProductName)
            .in(OzonAccrualReport::getAccrualId, ids)
            .isNotNull(OzonAccrualReport::getProductName)
            .groupBy(OzonAccrualReport::getAccrualId, OzonAccrualReport::getProductName)
            .orderByAsc(OzonAccrualReport::getProductName);
        Map<String, Set<String>> names = new HashMap<>();
        for (OzonAccrualReport row : accrualMapper.selectList(query)) {
            if (StringUtils.isNotBlank(row.getProductName())) {
                names.computeIfAbsent(row.getAccrualId(), key -> new LinkedHashSet<>()).add(row.getProductName().trim());
            }
        }
        for (OzonAccrualSummaryVo row : rows) {
            row.setProductName(String.join(" / ", names.getOrDefault(row.getAccrualId(), Set.of())));
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
