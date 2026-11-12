package org.dromara.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dromara.system.domain.bo.OzonOpsQuery;
import org.dromara.system.domain.vo.OzonOpsDailyVo;
import org.dromara.system.domain.vo.OzonOpsDetailVo;

import java.util.List;

/**
 * 「运营 / 1.运营情况」数据查询（表 {@code ozon_ops_daily}）。
 * <p>
 * 🚨 约定：{@code <where>} 只会去掉第一段内容开头的 AND，不会在两个 {@code <if>} 之间自动补 AND，
 * 所以非首位的 {@code <if>} 必须自己以 AND 开头。
 * 🚨 带 {@code <script>} 的 @Select 里不能出现 {@code <>}，大小比较一律写 {@code &gt;}/{@code &lt;}。
 */
public interface OzonOpsDailyMapper {

    /** 日期列表（倒序），只取摘要字段 —— 正文可能很长，列表接口不带。 */
    @Select("""
        <script>
        SELECT id, stat_date AS statDate, title, digest,
               orders, units, revenue, cancelled, aov,
               orders_prev AS ordersPrev, revenue_prev AS revenuePrev,
               orders_base7 AS ordersBase7, revenue_base7 AS revenueBase7,
               shop1_orders AS shop1Orders, shop1_revenue AS shop1Revenue,
               shop2_orders AS shop2Orders, shop2_revenue AS shop2Revenue,
               price_changes AS priceChanges, promo_active AS promoActive,
               ad_spent AS adSpent, risk_count AS riskCount,
               generated_at AS generatedAt
        FROM ozon_ops_daily
        <where>
          <if test="q.dateFrom != null">stat_date &gt;= #{q.dateFrom}</if>
          <if test="q.dateTo != null">AND stat_date &lt;= #{q.dateTo}</if>
        </where>
        ORDER BY stat_date DESC
        LIMIT #{q.limit}
        </script>
        """)
    List<OzonOpsDailyVo> selectList(@Param("q") OzonOpsQuery q);

    /** 单日全文（含 markdown 原文与预渲染 HTML）。 */
    @Select("""
        SELECT id, stat_date AS statDate, title, digest,
               orders, units, revenue, cancelled, aov,
               orders_prev AS ordersPrev, revenue_prev AS revenuePrev,
               orders_base7 AS ordersBase7, revenue_base7 AS revenueBase7,
               shop1_orders AS shop1Orders, shop1_revenue AS shop1Revenue,
               shop2_orders AS shop2Orders, shop2_revenue AS shop2Revenue,
               price_changes AS priceChanges, promo_active AS promoActive,
               ad_spent AS adSpent, risk_count AS riskCount,
               generated_at AS generatedAt,
               content_md AS contentMd, content_html AS contentHtml
        FROM ozon_ops_daily
        WHERE id = #{id}
        """)
    OzonOpsDetailVo selectById(@Param("id") Long id);
}
