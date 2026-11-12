package org.dromara.system.domain.bo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 「运营 / 1.运营情况」的筛选条件。
 * <p>
 * 数据源是 {@code ozon_ops_daily}（一天一条的运营简报，双店合并），见 mysql/25_*.sql。
 * 日期是**莫斯科日期**，与订单/广告等其它 Ozon 页面口径一致。
 */
@Data
public class OzonOpsQuery implements Serializable {
    /** 起始日期（含）；为空表示不限。 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;
    /** 截止日期（含）；为空表示不限。 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;
    /** 返回条数上限，默认 60（约两个月）。 */
    private Integer limit;
}
