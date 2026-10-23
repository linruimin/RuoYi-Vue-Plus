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
     * 订单图表下钻：某个卖家货号 / 某个月份下的**单个应计项目**列表，后端分页。
     * 一行 = 一条应计费用编号（在该月、该货号上的那部分费用），不再是原始费用行。
     * <p>
     * 数据源是下钻预聚合表 {@code ozon_accrual_item}（粒度 店铺 × 应计月 × 卖家货号 × 编号，
     * 见 mysql/16_ozon_accrual_item.sql）。为什么不用蒸馏表 {@code ozon_accrual_summary}：
     * <ul>
     *   <li>蒸馏表没有「月份」「货号」两维，一个编号可能跨月（实测 205 个），
     *       直接用它下钻会让「弹窗合计 ≠ 柱高」；</li>
     *   <li>蒸馏表只收 accrual_id 非空的行，会漏掉 133 行无编号费用（合计 -261,384.50，
     *       全在「未标注货号」柱里），那根柱子会差一半。</li>
     * </ul>
     * 该表按与图表完全相同的粒度预聚合，所以弹窗的「净额合计」严格等于柱高。
     * <p>
     * sku 为 null 表示不按货号过滤；sku 为空串表示只看「未标注货号」；month 为空表示不限月份。
     * accrual_id 为空串的行 = 「该月该货号下没有编号的费用」合成项，前端显示「（无编号）」。
     */
    @Select("""
        <script>
        SELECT i.min_row_id AS row_id, i.accrual_id, i.shop_id, i.stat_month, i.seller_sku,
               i.product_name, i.first_accrual_date, i.accrual_date,
               i.positive_amount_rub, i.negative_amount_rub, i.total_amount_rub,
               i.after_tax_amount_rub, i.total_amount_cny, i.record_count
        FROM ozon_accrual_item i
        <where>
          <if test="shopId != null">i.shop_id = #{shopId}</if>
          <if test="month != null and month != ''">AND i.stat_month = #{month}</if>
          <if test="sku != null">AND i.seller_sku = #{sku}</if>
        </where>
        </script>
        """)
    Page<OzonAccrualSummaryVo> selectChartRows(Page<OzonAccrualSummaryVo> page,
                                             @Param("shopId") Long shopId,
                                             @Param("sku") String sku,
                                             @Param("month") String month);
}
