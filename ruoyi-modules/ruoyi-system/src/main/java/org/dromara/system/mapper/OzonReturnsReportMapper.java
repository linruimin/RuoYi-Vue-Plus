package org.dromara.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.OzonBizReturns;
import org.dromara.system.domain.vo.OzonBizReturnsVo;
import org.dromara.system.domain.vo.OzonReturnsChartVo;
import org.dromara.system.domain.vo.OzonReturnsCostVo;
import org.dromara.system.domain.vo.OzonReturnsReportVo;

import java.util.List;

/**
 * 退货报表：把 ozon_returns 明细按「退货月份 × 店铺 × SKU / 货号」汇总成一行，
 * 再关联产品库取品名、编号与货品图片，并回填同月同 SKU 的售出件数用于计算退货率。
 */
public interface OzonReturnsReportMapper extends BaseMapperPlus<OzonBizReturns, OzonReturnsReportVo> {

    @Select("""
        SELECT r.*, p_view.id AS product_id, p_view.product_no AS product_no, p_view.name AS local_product_name,
          (SELECT s.`name` FROM shop s WHERE s.id = r.shop_id) AS shop_name,
          ROUND(r.return_qty / NULLIF(r.sold_units, 0) * 100, 2) AS return_rate,
          (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url))
             FROM attachment a
            WHERE a.source_table='product' AND a.feishu_record_id=p_view.feishu_record_id
              AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachment_json
        FROM (
          SELECT DATE_FORMAT(y.return_date, '%Y-%m-01') AS report_month, y.shop_id, y.sku, y.article_no,
            MAX(y.product_name) AS ozon_product_name,
            COUNT(*) AS shipment_count,
            SUM(y.return_qty) AS return_qty,
            SUM(CASE WHEN y.return_status = '被处理' THEN 1 ELSE 0 END) AS processed_count,
            SUM(CASE WHEN y.return_status IS NULL OR y.return_status <> '被处理' THEN 1 ELSE 0 END) AS pending_count,
            SUM(CASE WHEN y.return_status IN ('销毁中','我们已核销商品') THEN 1 ELSE 0 END) AS disposal_count,
            SUM(y.storage_fee_rub) AS storage_fee_rub,
            SUM(y.disposal_fee_rub) AS disposal_fee_rub,
            SUM(y.max_price_rub) AS max_price_rub,
            AVG(y.storage_days) AS avg_storage_days,
            DATE_FORMAT(MIN(y.return_date), '%Y-%m-%d %H:%i:%s') AS first_return_date,
            DATE_FORMAT(MAX(y.return_date), '%Y-%m-%d %H:%i:%s') AS last_return_date,
            (SELECT SUM(m.sold_units) FROM ozon_product_monthly_summary m
              WHERE m.ozon_sku = y.sku AND m.report_month = DATE_FORMAT(y.return_date, '%Y-%m-01')) AS sold_units
          FROM ozon_returns y
          WHERE y.return_date IS NOT NULL
          GROUP BY DATE_FORMAT(y.return_date, '%Y-%m-01'), y.shop_id, y.sku, y.article_no
        ) r
        LEFT JOIN product p_view ON p_view.id = (SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no = r.article_no)
        ${ew.customSqlSegment}
        """)
    Page<OzonReturnsReportVo> selectReportPage(Page<OzonReturnsReportVo> page, @Param("ew") Wrapper<OzonBizReturns> wrapper);

    /**
     * 退货图表「按月趋势」：按退货月份汇总件数与货件行数（不受月份筛选影响，始终展示全部月份）。
     * 注意：带 &lt;script&gt; 的 @Select 里不能出现 &lt;&gt;，字符串比较一律用 !=。
     */
    @Select("""
        <script>
        SELECT DATE_FORMAT(y.return_date, '%Y-%m') AS month,
               SUM(y.return_qty) AS return_qty,
               COUNT(*) AS shipment_count
        FROM ozon_returns y
        WHERE y.return_date IS NOT NULL
          <if test="shopId != null">AND y.shop_id = #{shopId}</if>
          <if test="status != null and status != ''">AND y.return_status = #{status}</if>
        GROUP BY DATE_FORMAT(y.return_date, '%Y-%m')
        ORDER BY month
        </script>
        """)
    List<OzonReturnsChartVo.MonthStat> selectMonthStats(@Param("shopId") Long shopId, @Param("status") String status);

    /**
     * 退货图表「按产品排行」：按卖家货号归并退货件数，关联产品库取品名与货品图片。
     * 店铺 / 状态 / 月份为空表示不过滤；月份格式 YYYY-MM。
     */
    @Select("""
        <script>
        SELECT r.article_no, r.sku, r.local_product_name, r.shipment_count, r.return_qty,
          (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url))
             FROM attachment a
            WHERE a.source_table='product' AND a.feishu_record_id=r.feishu_record_id
              AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url != '') AS attachment_json
        FROM (
          SELECT y.article_no AS article_no, MIN(y.sku) AS sku,
                 p.name AS local_product_name, p.feishu_record_id AS feishu_record_id,
                 COUNT(*) AS shipment_count, SUM(y.return_qty) AS return_qty
          FROM ozon_returns y
          LEFT JOIN product p ON p.id = (SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no = y.article_no)
          WHERE y.return_date IS NOT NULL
            <if test="shopId != null">AND y.shop_id = #{shopId}</if>
            <if test="status != null and status != ''">AND y.return_status = #{status}</if>
            <if test="month != null and month != ''">AND DATE_FORMAT(y.return_date, '%Y-%m') = #{month}</if>
          GROUP BY y.article_no, p.name, p.feishu_record_id
        ) r
        ORDER BY r.return_qty DESC
        </script>
        """)
    List<OzonReturnsChartVo.ProductStat> selectProductStats(@Param("shopId") Long shopId,
                                                            @Param("status") String status,
                                                            @Param("month") String month);

    /**
     * 退货图表下钻：某个卖家货号（或某个月份）下的退货明细行，复用「8.退货」业务视图的 Vo。
     * 品名取产品库中文名（覆盖明细里的俄文 product_name），店铺名按 shop 表回填。
     */
    @Select("""
        <script>
        SELECT b.*, p_view.id AS productId, p_view.product_no AS productNo, p_view.name AS productName,
          r_shop.`name` AS shopIdLabel,
          (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url))
             FROM attachment a
            WHERE a.source_table='product' AND a.feishu_record_id=p_view.feishu_record_id
              AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url != '') AS attachmentJson
        FROM ozon_returns b
        LEFT JOIN product p_view ON p_view.id = (SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no = b.article_no)
        LEFT JOIN shop r_shop ON r_shop.id = b.shop_id
        <where>
          <if test="articleNo != null and articleNo != ''">b.article_no = #{articleNo}</if>
          <if test="shopId != null">AND b.shop_id = #{shopId}</if>
          <if test="status != null and status != ''">AND b.return_status = #{status}</if>
          <if test="month != null and month != ''">AND DATE_FORMAT(b.return_date, '%Y-%m') = #{month}</if>
        </where>
        ORDER BY b.return_date DESC, b.row_id DESC
        </script>
        """)
    List<OzonBizReturnsVo> selectChartRows(@Param("shopId") Long shopId, @Param("status") String status,
                                           @Param("month") String month, @Param("articleNo") String articleNo);

    /**
     * 退货成本：退货侧汇总（单数 / 件数 / 货值 / 仓储费 / 销毁费）。
     * 店铺、状态、月份为空表示不过滤；月份按 return_date 归月，与「2.1 退货图表」「8 退货」一致。
     */
    @Select("""
        <script>
        SELECT COUNT(*) AS shipmentCount,
               COALESCE(SUM(y.return_qty), 0) AS returnQty,
               COALESCE(SUM(y.max_price_rub), 0) AS goodsValueRub,
               COALESCE(SUM(y.storage_fee_rub), 0) AS storageFeeRub,
               COALESCE(SUM(y.disposal_fee_rub), 0) AS disposalFeeRub
        FROM ozon_returns y
        WHERE y.return_date IS NOT NULL
          <if test="shopId != null">AND y.shop_id = #{shopId}</if>
          <if test="status != null and status != ''">AND y.return_status = #{status}</if>
          <if test="month != null and month != ''">AND DATE_FORMAT(y.return_date, '%Y-%m') = #{month}</if>
        </script>
        """)
    java.util.Map<String, Object> selectCostReturnSummary(@Param("shopId") Long shopId,
                                                          @Param("status") String status,
                                                          @Param("month") String month);

    /**
     * 退货成本：把退货单按「店铺 + 卖家货号 + 订单日期」分组计数，作为应计费用的分摊基数。
     * 应计没有货件号，只能落到这个粒度上均摊。
     */
    @Select("""
        <script>
        SELECT y.shop_id AS shopId,
               y.article_no AS articleNo,
               DATE_FORMAT(y.order_date, '%Y-%m-%d') AS orderDate,
               COUNT(*) AS cnt
        FROM ozon_returns y
        WHERE y.return_date IS NOT NULL
          AND y.order_date IS NOT NULL
          <if test="shopId != null">AND y.shop_id = #{shopId}</if>
          <if test="status != null and status != ''">AND y.return_status = #{status}</if>
          <if test="month != null and month != ''">AND DATE_FORMAT(y.return_date, '%Y-%m') = #{month}</if>
        GROUP BY y.shop_id, y.article_no, DATE_FORMAT(y.order_date, '%Y-%m-%d')
        </script>
        """)
    List<java.util.Map<String, Object>> selectCostReturnGroups(@Param("shopId") Long shopId,
                                                               @Param("status") String status,
                                                               @Param("month") String month);

    /**
     * 退货成本：应计侧按费用类型汇总（5 个退货相关科目，含销售侧冲销的收入退回 / 佣金返还）。
     * 月份按 accrual_date（出账日期）过滤，与退货侧的 return_date 不是同一口径，页面已注明。
     */
    @Select("""
        <script>
        SELECT a.shop_id AS shopId, a.accrual_type AS accrualType,
               COUNT(*) AS cnt, COALESCE(SUM(a.total_amount_rub), 0) AS amount
        FROM ozon_accruals a
        WHERE a.accrual_type IN ('逆向物流', '合作伙伴处理退货、取消和拒收', 'PickUpPointReturnAcceptance',
                                 '收入退回', '佣金返还')
          <if test="shopId != null">AND a.shop_id = #{shopId}</if>
          <if test="month != null and month != ''">AND DATE_FORMAT(a.accrual_date, '%Y-%m') = #{month}</if>
        GROUP BY a.shop_id, a.accrual_type
        </script>
        """)
    List<java.util.Map<String, Object>> selectCostAccrualTotals(@Param("shopId") Long shopId,
                                                                @Param("month") String month);

    /**
     * 退货成本：应计侧按「店铺 + 卖家货号 + 订单日期 + 科目」分组，供分摊到具体退货单。
     * 订单日期为空的应计行无法归属，由 service 计入「未分摊」。
     */
    @Select("""
        <script>
        SELECT a.shop_id AS shopId, a.seller_sku AS articleNo,
               DATE_FORMAT(a.order_accepted_or_service_date, '%Y-%m-%d') AS orderDate,
               a.accrual_type AS accrualType,
               COUNT(*) AS cnt, COALESCE(SUM(a.total_amount_rub), 0) AS amount
        FROM ozon_accruals a
        WHERE a.accrual_type IN ('逆向物流', '合作伙伴处理退货、取消和拒收', 'PickUpPointReturnAcceptance')
          <if test="shopId != null">AND a.shop_id = #{shopId}</if>
          <if test="month != null and month != ''">AND DATE_FORMAT(a.accrual_date, '%Y-%m') = #{month}</if>
        GROUP BY a.shop_id, a.seller_sku,
                 DATE_FORMAT(a.order_accepted_or_service_date, '%Y-%m-%d'), a.accrual_type
        </script>
        """)
    List<java.util.Map<String, Object>> selectCostAccrualGroups(@Param("shopId") Long shopId,
                                                                @Param("month") String month);

    /**
     * 退货成本明细：一行 = 一张退货单，带产品库中文名、货品图片与店铺名。
     * 分摊成本由 service 按「货号 + 订单日」分组计算后回填，SQL 只负责取明细。
     */
    @Select("""
        <script>
        SELECT b.shop_id AS shopId, b.shipment_no AS shipmentNo, b.article_no AS articleNo, b.sku AS sku,
               COALESCE(p_view.name, b.product_name) AS productName,
               b.return_status AS returnStatus,
               DATE_FORMAT(b.return_date, '%Y-%m-%d %H:%i:%s') AS returnDate,
               DATE_FORMAT(b.status_date, '%Y-%m-%d %H:%i:%s') AS statusDate,
               b.storage_days AS storageDays, b.return_qty AS returnQty,
               b.storage_fee_rub AS storageFeeRub,
               DATE_FORMAT(b.order_date, '%Y-%m-%d') AS orderDate,
               r_shop.`name` AS shopName,
               (SELECT a.cos_url FROM attachment a
                 WHERE a.source_table='product' AND a.feishu_record_id=p_view.feishu_record_id
                   AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url != ''
                 ORDER BY a.id LIMIT 1) AS imageUrl
        FROM ozon_returns b
        LEFT JOIN product p_view ON p_view.id = (SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no = b.article_no)
        LEFT JOIN shop r_shop ON r_shop.id = b.shop_id
        WHERE b.return_date IS NOT NULL
          <if test="shopId != null">AND b.shop_id = #{shopId}</if>
          <if test="status != null and status != ''">AND b.return_status = #{status}</if>
          <if test="month != null and month != ''">AND DATE_FORMAT(b.return_date, '%Y-%m') = #{month}</if>
        ORDER BY b.return_date DESC, b.row_id DESC
        </script>
        """)
    List<OzonReturnsCostVo.CostRow> selectCostRows(@Param("shopId") Long shopId,
                                                   @Param("status") String status,
                                                   @Param("month") String month);

}
