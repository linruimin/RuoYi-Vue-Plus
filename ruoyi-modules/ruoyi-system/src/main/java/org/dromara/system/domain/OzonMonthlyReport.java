package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 产品月报只读映射；来源表没有若依审计字段，因此不继承 BaseEntity。 */
@Data
@TableName("ozon_product_monthly_summary")
public class OzonMonthlyReport implements Serializable {
    /** 记录编号。 */
    @TableId("row_id")
    private Long rowId;

    /** 统计月份。 */
    private LocalDate reportMonth;

    /** 卖家SKU。 */
    private String sellerSku;

    /** Ozon SKU。 */
    private Long ozonSku;

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
}
