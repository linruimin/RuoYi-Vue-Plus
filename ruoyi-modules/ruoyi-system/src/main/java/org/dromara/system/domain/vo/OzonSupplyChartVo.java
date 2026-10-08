package org.dromara.system.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 交货图表数据：同一次查询给出两个维度的汇总，保证口径一致——
 * 「按月趋势」不受月份筛选影响（始终展示全部月份），「按产品排行」受月份筛选影响。
 * 归月口径取明细的「完成日期」（completion_date），完成日期为空的行（在发运点 / 已准备发运 / 输入数据）不参与按月汇总。
 */
@Data
public class OzonSupplyChartVo implements Serializable {
    /** 按交货完成月份汇总，供趋势柱状图；月份升序。 */
    private List<MonthStat> months;

    /** 按卖家货号归并的产品交货数量，供排行柱状图；交货数量倒序。 */
    private List<OzonSupplyStatsVo> products;

    /** 单个月份的交货汇总。 */
    @Data
    public static class MonthStat implements Serializable {
        /** 交货完成月份（YYYY-MM）。 */
        private String month;
        /** 交货数量合计。 */
        private Integer totalQuantity;
        /** 交货申请数（按申请内部ID去重）。 */
        private Integer orderCount;
    }
}
