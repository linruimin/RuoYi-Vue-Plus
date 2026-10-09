package org.dromara.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dromara.system.domain.OzonAccrualReport;
import org.dromara.system.domain.bo.OzonReportQuery;
import org.dromara.system.domain.vo.OzonAccrualChartVo;
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
     * 订单图表「按月趋势」：按应计日期月份汇总总计（RUB）净额与费用编号数。
     * 始终展示全部月份，不受月份筛选影响；费用分组为空表示不限分组。
     * 注意：带 &lt;script&gt; 的 @Select 里不能出现 &lt;&gt;，字符串比较一律用 !=。
     */
    @Select("""
        <script>
        SELECT DATE_FORMAT(a.accrual_date, '%Y-%m') AS month,
               SUM(a.total_amount_rub) AS total_amount_rub,
               COUNT(DISTINCT a.accrual_id) AS accrual_count
        FROM ozon_accruals a
        <where>
          <if test="shopId != null">a.shop_id = #{shopId}</if>
          <if test="serviceGroup != null and serviceGroup != ''">AND a.service_group = #{serviceGroup}</if>
        </where>
        GROUP BY DATE_FORMAT(a.accrual_date, '%Y-%m')
        ORDER BY month
        </script>
        """)
    List<OzonAccrualChartVo.MonthStat> selectChartMonthStats(@Param("shopId") Long shopId,
                                                            @Param("serviceGroup") String serviceGroup);

    /**
     * 订单图表「按货号排行」：按卖家货号聚合总计（RUB）净额，供图表展示（只读、不分页）。
     * 月份为空表示不按应计月份过滤；产品库品名与图片按「卖家货号 = product.article_no」关联，取最小 id 防重。
     */
    @Select("""
        <script>
        SELECT r.sku, r.ozon_product_name, r.total_amount_rub, r.accrual_count,
               p.name AS product_name,
          (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url))
             FROM attachment a
            WHERE a.source_table='product' AND a.feishu_record_id=p.feishu_record_id
              AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url != '') AS attachment_json
        FROM (
          SELECT COALESCE(NULLIF(TRIM(a.seller_sku), ''), '') AS sku,
                 COALESCE(MAX(NULLIF(TRIM(a.product_name), '')), '') AS ozon_product_name,
                 SUM(a.total_amount_rub) AS total_amount_rub,
                 COUNT(DISTINCT a.accrual_id) AS accrual_count
          FROM ozon_accruals a
          <where>
            <if test="shopId != null">a.shop_id = #{shopId}</if>
            <if test="serviceGroup != null and serviceGroup != ''">AND a.service_group = #{serviceGroup}</if>
            <if test="month != null and month != ''">AND DATE_FORMAT(a.accrual_date, '%Y-%m') = #{month}</if>
          </where>
          GROUP BY COALESCE(NULLIF(TRIM(a.seller_sku), ''), '')
        ) r
        LEFT JOIN product p ON p.id = (SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no = r.sku COLLATE utf8mb4_unicode_ci)
        ORDER BY r.total_amount_rub DESC
        </script>
        """)
    List<OzonAccrualChartVo.ProductStat> selectChartProductStats(@Param("shopId") Long shopId,
                                                                @Param("serviceGroup") String serviceGroup,
                                                                @Param("month") String month);

    /**
     * 订单图表下钻：某个卖家货号（或某个月份）下的订单费用原始明细行，分页返回。
     * 单货号单月最多 3.5 万行，必须后端分页，不能一次性返回。
     * sku 为 null 表示不过滤；sku 为空串表示只看「没有卖家货号」的明细。
     */
    @Select("""
        <script>
        SELECT a.row_id, a.shop_id, a.accrual_id, a.accrual_date, a.service_group, a.accrual_type,
               a.seller_sku, a.ozon_sku, a.product_name, a.quantity, a.total_amount_rub
        FROM ozon_accruals a
        <where>
          <if test="shopId != null">a.shop_id = #{shopId}</if>
          <if test="serviceGroup != null and serviceGroup != ''">AND a.service_group = #{serviceGroup}</if>
          <if test="month != null and month != ''">AND DATE_FORMAT(a.accrual_date, '%Y-%m') = #{month}</if>
          <if test="sku != null">AND COALESCE(NULLIF(TRIM(a.seller_sku), ''), '') = #{sku}</if>
        </where>
        </script>
        """)
    Page<OzonAccrualReportVo> selectChartRows(Page<OzonAccrualReportVo> page,
                                             @Param("shopId") Long shopId,
                                             @Param("serviceGroup") String serviceGroup,
                                             @Param("sku") String sku,
                                             @Param("month") String month);
}
