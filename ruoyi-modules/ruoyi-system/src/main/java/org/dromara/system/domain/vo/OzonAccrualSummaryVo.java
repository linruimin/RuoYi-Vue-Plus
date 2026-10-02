package org.dromara.system.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/** 按应计费用编号归集的完整正负金额。 */
@Data
public class OzonAccrualSummaryVo implements Serializable {
    /** 组内最小记录编号，空费用编号时用于唯一定位。 */
    private String rowId;
    /** 店铺 → shop.id。 */
    private Long shopId;
    /** 店铺名称。 */
    private String shopName;
    /** 应计费用编号。 */
    private String accrualId;
    /** 同一费用编号关联的商品名称，多个名称完整去重展示。 */
    private String productName;
    /** 组内最早应计日期。 */
    private LocalDate firstAccrualDate;
    /** 组内最近应计日期，作为默认排序日期。 */
    private LocalDate accrualDate;
    /** 所有正金额之和。 */
    private BigDecimal positiveAmountRub;
    /** 所有负金额之和，保留负号。 */
    private BigDecimal negativeAmountRub;
    /** 正负金额净合计。 */
    private BigDecimal totalAmountRub;
    /** 总计减去销售额乘以0.075，不提前舍入。 */
    private BigDecimal afterTaxAmountRub;
    /** 总计-税后乘以0.075，不提前舍入。 */
    private BigDecimal totalAmountCny;
    /** 原始明细行数。 */
    private Long recordCount;
}
