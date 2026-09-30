package org.dromara.system.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/** 当前费用筛选范围内可查看逐笔销售的产品。 */
@Data
public class OzonSalesProductVo implements Serializable {
    /** 产品稳定标识。 */
    private String productKey;
    /** 商品名称。 */
    private String productName;
    /** 卖家SKU。 */
    private String sellerSku;
    /** 费用编号去重后的笔数。 */
    private Long saleCount;
    /** 此产品全部逐笔销售额合计（RUB）。 */
    private BigDecimal salesAmountRub;
}
