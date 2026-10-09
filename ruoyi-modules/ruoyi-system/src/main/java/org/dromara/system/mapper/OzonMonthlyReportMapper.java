package org.dromara.system.mapper;

import org.dromara.system.domain.OzonMonthlyReport;
import org.dromara.system.domain.vo.OzonAccrualChartVo;
import org.dromara.system.domain.vo.OzonMonthlyReportVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/** 产品月报数据查询。 */
public interface OzonMonthlyReportMapper extends BaseMapperPlus<OzonMonthlyReport, OzonMonthlyReportVo> {
    /** 复用产品成本视图，按飞书“货号=卖家SKU”关联，避免多产品重复展开月报记录。 */
    @Select("""
        SELECT r.* FROM (
          SELECT (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='product' AND a.feishu_record_id=p.feishu_record_id AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachment_json,m.*, CONCAT(DATE_FORMAT(m.report_month,'%Y-%m'),' / ',m.seller_sku) AS record_name,
            p.name AS local_product_name, v.`平均合计成本` AS average_cost,
            m.final_take_home_cny / NULLIF(m.sold_units,0) AS unit_take_home_cny,
            ROUND(m.final_take_home_cny / NULLIF(m.sold_units,0) / NULLIF(v.`平均合计成本`,0),2) AS unit_multiple,
            v.`平均合计成本` * m.sold_units AS total_cost,
            m.final_take_home_cny - v.`平均合计成本` * m.sold_units AS total_profit
          FROM ozon_product_monthly_summary m
          LEFT JOIN product p ON p.id=(SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no=m.seller_sku)
          LEFT JOIN v_app_product v ON v.id=p.id
        ) r ${ew.customSqlSegment}
        """)
    Page<OzonMonthlyReportVo> selectViewPage(Page<OzonMonthlyReportVo> page, @Param("ew") Wrapper<OzonMonthlyReport> wrapper);

    /**
     * 汇总图表（0.0）「订单数量（按月）」：取产品月报的「售出件数」（sold_units）。
     * <p>
     * 为什么不直接数应计明细的订单编号：{@code ozon_accruals} 是按「费用」铺开的，
     * 一笔销售会横跨「销售收入 / 销售佣金 / 折扣积分 / 物流 / 收单服务…」七八行，
     * 而且除了销售还有大量跟卖货无关的费用单据（合作伙伴服务、商品销毁、包装材料…）。
     * 无论怎么按编号去重，都会把服务费单据当成订单（去重后 76,747 里有 30,856 是服务费编号），
     * 且 {@code quantity} 在这张表里几乎恒为 1，「数量」实际等于「笔数」。
     * 产品月报的 sold_units 才是「这个货号这个月真实卖出多少件」，与交货 / 退货同为「件」，可直接比较。
     * <p>
     * 产品月报没有店铺字段，店铺维度用应计表把货号映射回店铺
     * （本项目货号不跨店铺，映射唯一），这样选店铺时订单侧与交货 / 退货口径一致。
     * 同一个「月 + 货号」在产品月报里可能是多行（如 GUOLVQI001），所以必须 SUM 而不是取一行。
     * 注意：带 &lt;script&gt; 的 @Select 里不能出现 &lt;&gt;，字符串比较一律用 !=。
     */
    @Select("""
        <script>
        SELECT DATE_FORMAT(m.report_month, '%Y-%m') AS month,
               SUM(m.sold_units) AS total_quantity,
               SUM(m.sales_record_count) AS accrual_count
        FROM ozon_product_monthly_summary m
        <where>
          <if test="shopId != null">
            EXISTS (SELECT 1 FROM ozon_accruals a
                     WHERE a.shop_id = #{shopId}
                       AND TRIM(a.seller_sku) COLLATE utf8mb4_unicode_ci = TRIM(m.seller_sku) COLLATE utf8mb4_unicode_ci)
          </if>
        </where>
        GROUP BY DATE_FORMAT(m.report_month, '%Y-%m')
        ORDER BY month
        </script>
        """)
    List<OzonAccrualChartVo.MonthStat> selectSoldUnitsMonths(@Param("shopId") Long shopId);

    /**
     * 汇总图表（0.0）「订单数量（按货号）」：口径同 {@link #selectSoldUnitsMonths}。
     * 品名与图片的兜底逻辑与订单图表一致（产品库优先，退回月报自带的商品名称）。
     * <p>
     * 🚨 {@code <where>} 只会去掉**第一段**内容的开头 AND / OR，不会在两个 {@code <if>} 之间自动补 AND，
     * 所以每个非首位的 {@code <if>} 必须自己以 AND 开头。这里曾漏写 → 选店铺 + 选月份同时出现时
     * 拼出 {@code WHERE EXISTS (...) DATE_FORMAT(...)} → BadSqlGrammarException → 接口 500、页面整页变空。
     */
    @Select("""
        <script>
        SELECT t.sku, t.total_quantity, t.accrual_count, t.ozon_product_name,
               p.name AS product_name,
          (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a3.id,'fileName',a3.file_name,'mimeType',a3.mime_type,'cosUrl',a3.cos_url))
             FROM attachment a3
            WHERE a3.source_table='product' AND a3.feishu_record_id=p.feishu_record_id
              AND a3.field_name='货品图片' AND a3.cos_url IS NOT NULL AND a3.cos_url != '') AS attachment_json
        FROM (
          SELECT TRIM(m.seller_sku) AS sku,
                 SUM(m.sold_units) AS total_quantity,
                 SUM(m.sales_record_count) AS accrual_count,
                 COALESCE(MAX(NULLIF(TRIM(m.product_name), '')), '') AS ozon_product_name
          FROM ozon_product_monthly_summary m
          <where>
            <if test="shopId != null">
              EXISTS (SELECT 1 FROM ozon_accruals a
                       WHERE a.shop_id = #{shopId}
                         AND TRIM(a.seller_sku) COLLATE utf8mb4_unicode_ci = TRIM(m.seller_sku) COLLATE utf8mb4_unicode_ci)
            </if>
            <if test="month != null and month != ''">AND DATE_FORMAT(m.report_month, '%Y-%m') = #{month}</if>
          </where>
          GROUP BY TRIM(m.seller_sku)
        ) t
        LEFT JOIN product p ON p.id = (SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no = t.sku COLLATE utf8mb4_unicode_ci)
        ORDER BY t.total_quantity DESC
        </script>
        """)
    List<OzonAccrualChartVo.ProductStat> selectSoldUnitsProducts(@Param("shopId") Long shopId,
                                                                @Param("month") String month);
}
