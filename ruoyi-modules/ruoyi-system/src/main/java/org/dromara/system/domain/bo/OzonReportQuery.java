package org.dromara.system.domain.bo;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.time.LocalDate;

/** 三类外部只读报表的筛选条件，不用于实体写入。 */
@Data
public class OzonReportQuery implements Serializable {
    /** 卖家 SKU 或交货 ItemCode，精确匹配。 */
    @Size(max = 255)
    private String sellerSku;
    /** 逐笔销售趋势的产品标识，由产品列表接口返回。 */
    @Size(max = 300)
    private String productKey;
    /** Ozon SKU，精确匹配。 */
    @Pattern(regexp = "^$|[0-9]{1,20}", message = "SKU必须为数字")
    private String ozonSku;
    /** 商品名称关键字。 */
    @Size(max = 200)
    private String productName;
    /** 统计月份，以该月任意一天表示。 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate reportMonth;
    /** 应计起始日期，包含当天。 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    /** 应计截止日期，包含当天。 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    /** 应计费用编号。 */
    @Size(max = 32)
    private String accrualId;
    /** 服务分组。 */
    @Size(max = 100)
    private String serviceGroup;
    /** 应计类型。 */
    @Size(max = 255)
    private String accrualType;
    /** 交货申请编号。 */
    @Size(max = 64)
    private String applicationNo;
    /** 交货申请状态。 */
    @Size(max = 32)
    private String status = "已完成";
    /** 报表分组：month、sku 或 none。 */
    @Pattern(regexp = "month|sku|none", message = "分组方式不正确")
    private String groupBy = "month";
    /** 分组顺序；null沿用既有报表默认顺序。 */
    private Boolean groupDesc;
    /** 无应计费用编号时定位独立明细行。 */
    private Long rowId;
    /** 全局店铺范围，仅订单费用明细生效；为空表示不限制店铺。 */
    private Long scopeShopId;
}
