package org.dromara.system.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 退货报表汇总行：一行 = 一个「退货月份 × SKU（货号）」。
 * 数据来自 ozon_returns（Ozon 后台退货与取消报表同步表），只读。
 */
@Data
public class OzonReturnsReportVo implements Serializable {
    /** 退货月份（该月 1 日，YYYY-MM-01）。 */
    private String reportMonth;
    /** 店铺。 */
    private Long shopId;
    /** 店铺名称。 */
    private String shopName;
    /** 货号（关联产品库）。 */
    private String articleNo;
    /** Ozon SKU。 */
    private String sku;
    /** 关联产品库的品名。 */
    private String localProductName;
    /** Ozon 报表原始商品名称（俄文）。 */
    private String ozonProductName;
    /** 退货货件数。 */
    private Long shipmentCount;
    /** 退货件数。 */
    private Long returnQty;
    /** 已处理件数（退货状态=被处理）。 */
    private Long processedCount;
    /** 未完结件数（被处理以外的状态）。 */
    private Long pendingCount;
    /** 销毁件数（销毁中 / 已核销商品）。 */
    private Long disposalCount;
    /** 同月同 SKU 的售出件数（取自产品月报）。 */
    private Long soldUnits;
    /** 退货率（%，退货件数 ÷ 售出件数）。 */
    private BigDecimal returnRate;
    /** 仓储费用合计（RUB）。 */
    private BigDecimal storageFeeRub;
    /** 销毁费用合计（RUB）。 */
    private BigDecimal disposalFeeRub;
    /** 货值合计（RUB，按报表「最高价格」求和）。 */
    private BigDecimal maxPriceRub;
    /** 平均存储天数。 */
    private BigDecimal avgStorageDays;
    /** 首次退货日期（YYYY-MM-DD HH:mm:ss）。 */
    private String firstReturnDate;
    /** 最近退货日期（YYYY-MM-DD HH:mm:ss）。 */
    private String lastReturnDate;
    /** 关联产品的记录ID。 */
    private Long productId;
    /** 关联产品的产品编号。 */
    private Long productNo;
    /** 关联产品图片（attachment 表公开文件信息），仅用于图片预览。 */
    private String attachmentJson;
}
