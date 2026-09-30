package org.dromara.system.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/** 当前费用汇总筛选范围内的产品月度销售额。 */
@Data
public class OzonProductSalesTrendVo implements Serializable {
    /** 产品稳定标识，优先使用卖家SKU。 */
    private String productKey;
    /** 商品名称，缺失时显示未标注商品。 */
    private String productName;
    /** 卖家SKU。 */
    private String sellerSku;
    /** 费用编号最近应计日期所属月份，格式yyyy-MM。 */
    private String reportMonth;
    /** 销售额（RUB），与原视图一致，仅累计正金额。 */
    private BigDecimal salesAmountRub;
}
