package org.dromara.system.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/** 一个产品在一个费用编号下的销售额，合并收入、积分等正金额明细。 */
@Data
public class OzonProductSaleVo implements Serializable {
    /** 原始明细最小行号，用于同一天的稳定排序，不表示日内成交顺序。 */
    private Long rowId;
    /** 产品稳定标识。 */
    private String productKey;
    /** 商品名称。 */
    private String productName;
    /** 卖家SKU。 */
    private String sellerSku;
    /** 应计费用编号。 */
    private String accrualId;
    /** 订单受理日期，缺失时回退服务日期或应计日期。 */
    private LocalDate saleDate;
    /** 该笔正金额明细最近应计日期。 */
    private LocalDate accrualDate;
    /** 销售额（RUB），沿用原费用视图的正金额合计口径。 */
    private BigDecimal salesAmountRub;
}
