package org.dromara.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dromara.system.domain.OzonAccrualReport;
import org.dromara.system.domain.bo.OzonReportQuery;
import org.dromara.system.domain.vo.OzonAccrualReportVo;
import org.dromara.system.domain.vo.OzonAccrualSummaryVo;
import org.dromara.system.domain.vo.OzonProductSalesTrendVo;
import java.util.List;
import org.dromara.system.domain.vo.OzonProductSaleVo;
import org.dromara.system.domain.vo.OzonSalesProductVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/** 订单费用明细及按编号归集查询。 */
public interface OzonAccrualReportMapper extends BaseMapperPlus<OzonAccrualReport, OzonAccrualReportVo> {
    /** 聚合后筛选日期，确保同一费用编号的所有正负金额完整参与合计。 */
    Page<OzonAccrualSummaryVo> selectSummary(Page<OzonAccrualSummaryVo> page, @Param("q") OzonReportQuery query);
    /** 复用原视图筛选后的全部费用编号，按产品和月份汇总销售额。 */
    List<OzonProductSalesTrendVo> selectSalesTrend(@Param("q") OzonReportQuery query);
    /** 当前费用筛选范围内的产品及逐笔销售统计。 */
    List<OzonSalesProductVo> selectSalesProducts(@Param("q") OzonReportQuery query);
    /** 单产品逐笔销售，不对同一天的多笔销售再聚合。 */
    List<OzonProductSaleVo> selectSalesTransactions(@Param("q") OzonReportQuery query);

    /**
     * 订单图表下钻：某个卖家货号（或某个月份）下的订单费用原始明细行，分页返回。
     * 单货号单月最多 3.5 万行，必须后端分页，不能一次性返回。
     * sku 为 null 表示不过滤；sku 为空串表示只看「没有卖家货号」的明细。
     * <p>
     * ⚠️ 这里读的是**源表** ozon_accruals，不是蒸馏表 ozon_accrual_summary：下钻要的是
     * 「逐条费用」的原始行，而蒸馏表一行 = 一条应计费用编号（已按编号合并），粒度不同。
     * 图表上方的按月趋势与按货号排行另走产品月报（见 OzonMonthlyReportMapper）。
     */
    @Select("""
        <script>
        SELECT a.row_id, a.shop_id, a.accrual_id, a.accrual_date, a.service_group, a.accrual_type,
               a.seller_sku, a.ozon_sku, a.product_name, a.quantity, a.total_amount_rub
        FROM ozon_accruals a
        <where>
          <if test="shopId != null">a.shop_id = #{shopId}</if>
          <if test="month != null and month != ''">AND DATE_FORMAT(a.accrual_date, '%Y-%m') = #{month}</if>
          <if test="sku != null">AND COALESCE(NULLIF(TRIM(a.seller_sku), ''), '') = #{sku}</if>
        </where>
        </script>
        """)
    Page<OzonAccrualReportVo> selectChartRows(Page<OzonAccrualReportVo> page,
                                             @Param("shopId") Long shopId,
                                             @Param("sku") String sku,
                                             @Param("month") String month);
}
