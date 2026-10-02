package org.dromara.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.OzonAccrualReport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 订单费用明细展示数据。 */
@Data
@AutoMapper(target = OzonAccrualReport.class)
public class OzonAccrualReportVo implements Serializable {
    /** 记录编号。 */
    private String rowId;

    /** 店铺 → shop.id。 */
    private Long shopId;

    /** 店铺名称（按 shop 表回填）。 */
    private String shopName;

    /** 应计费用编号。 */
    private String accrualId;

    /** 应计日期。 */
    private LocalDate accrualDate;

    /** 服务分组。 */
    private String serviceGroup;

    /** 应计类型。 */
    private String accrualType;

    /** 卖家SKU。 */
    private String sellerSku;

    /** Ozon SKU。 */
    private String ozonSku;

    /** 商品名称。 */
    private String productName;

    /** 数量。 */
    private BigDecimal quantity;

    /** 卖家价格（RUB）。 */
    private BigDecimal sellerPriceRub;

    /** 订单受理或服务日期。 */
    private LocalDate orderAcceptedOrServiceDate;

    /** 销售平台。 */
    private String salesPlatform;

    /** 履约方案。 */
    private String fulfillmentScheme;

    /** Ozon佣金比例。 */
    private BigDecimal ozonCommissionPct;

    /** 本地化指数比例。 */
    private BigDecimal localizationIndexPct;

    /** 平均配送时长（小时）。 */
    private BigDecimal averageDeliveryTimeHours;

    /** 应计金额（RUB）。 */
    private BigDecimal totalAmountRub;

    /** 导入时间。 */
    private LocalDateTime importedAt;
}
