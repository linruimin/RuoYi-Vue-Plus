package org.dromara.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.OzonMonthlyReport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 产品月报展示数据。 */
@Data
@AutoMapper(target = OzonMonthlyReport.class)
public class OzonMonthlyReportVo implements Serializable {
    /** 原视图记录名称：月份与卖家SKU。 */
    private String recordName;
    /** 按卖家SKU匹配产品货号得到的内部品名。 */
    private String localProductName;
    /** 单个最终到手人民币，零销量时为空。 */
    private BigDecimal unitTakeHomeCny;
    /** 对应产品的平均合计成本。 */
    private BigDecimal averageCost;
    /** 单个到手与平均成本之比，零成本时为空。 */
    private BigDecimal unitMultiple;
    /** 平均合计成本乘售出件数。 */
    private BigDecimal totalCost;
    /** 最终到手人民币减总成本。 */
    private BigDecimal totalProfit;
    /** 记录编号。 */
    private String rowId;

    /** 店铺 id（→ shop.id）；列表按它过滤「全局店铺」。 */
    private Long shopId;
    /** 店铺名称，列表里展示用。 */
    private String shopName;

    /** 统计月份。 */
    private LocalDate reportMonth;

    /** 卖家SKU。 */
    private String sellerSku;

    /** Ozon SKU。 */
    private String ozonSku;

    /** 商品名称。 */
    private String productName;

    /** 售出件数。 */
    private Long soldUnits;

    /** 销售记录数。 */
    private Integer salesRecordCount;

    /** 关联应计正合计（RUB）。 */
    private BigDecimal positiveAccrualTotalRub;

    /** 销售收入（RUB）。 */
    private BigDecimal salesRevenueRub;

    /** 折扣积分（RUB）。 */
    private BigDecimal discountPointsRub;

    /** 合作伙伴收入（RUB）。 */
    private BigDecimal partnerProgramsRub;

    /** 其他应计（RUB）。 */
    private BigDecimal otherAccrualsRub;

    /** 净销售收入（RUB）。 */
    private BigDecimal netSalesIncomeRub;

    /** 税额（RUB）。 */
    private BigDecimal taxRub;

    /** 最终到手（RUB）。 */
    private BigDecimal finalTakeHomeRub;

    /** 最终到手（CNY）。 */
    private BigDecimal finalTakeHomeCny;

    /** 更新时间。 */
    private LocalDateTime importedAt;
    /** 关联产品货品图片的公开文件信息。 */
    private String attachmentJson;
}
