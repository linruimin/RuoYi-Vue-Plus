package org.dromara.system.mapper;

import org.dromara.system.domain.OzonSupplyReport;
import org.dromara.system.domain.vo.OzonSupplyChartVo;
import org.dromara.system.domain.vo.OzonSupplyReportVo;
import org.dromara.system.domain.vo.OzonSupplyStatsVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/** 交货申请明细数据查询。 */
public interface OzonSupplyReportMapper extends BaseMapperPlus<OzonSupplyReport, OzonSupplyReportVo> {
    /** 按卖家货号（sku）关联唯一产品并取其货品图片与品名，避免图片附件导致报表记录重复。 */
    @Select("""
        SELECT r.* FROM (
          SELECT s.*, p.name AS local_product_name, (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='product' AND a.feishu_record_id=p.feishu_record_id AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachment_json
          FROM ozon_supply_product_details_20260914 s
          LEFT JOIN product p ON p.id=COALESCE((SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no=s.sku COLLATE utf8mb4_unicode_ci),(SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no=s.item_code COLLATE utf8mb4_unicode_ci))
        ) r ${ew.customSqlSegment}
        """)
    Page<OzonSupplyReportVo> selectViewPage(Page<OzonSupplyReport> page, @Param("ew") Wrapper<OzonSupplyReport> wrapper);

    /**
     * 交货图表「按月趋势」：按完成日期月份汇总交货数量与申请数（不受月份筛选影响，始终展示全部月份）。
     * 归月口径是明细的「完成日期」completion_date（DD.MM.YYYY），为空的行不参与汇总。
     * 注意：带 &lt;script&gt; 的 @Select 里不能出现 &lt;&gt;，字符串比较一律用 !=。
     */
    @Select("""
        <script>
        SELECT DATE_FORMAT(STR_TO_DATE(s.completion_date, '%d.%m.%Y'), '%Y-%m') AS month,
               SUM(s.quantity) AS total_quantity,
               COUNT(DISTINCT s.order_id) AS order_count
        FROM ozon_supply_product_details_20260914 s
        WHERE s.completion_date IS NOT NULL AND s.completion_date != ''
          <if test="shopId != null">AND s.shop_id = #{shopId}</if>
          <if test="status != null and status != ''">AND s.status = #{status}</if>
        GROUP BY DATE_FORMAT(STR_TO_DATE(s.completion_date, '%d.%m.%Y'), '%Y-%m')
        ORDER BY month
        </script>
        """)
    List<OzonSupplyChartVo.MonthStat> selectMonthStats(@Param("shopId") Long shopId, @Param("status") String status);

    /** 交货图表「按产品排行」：按卖家货号聚合交货数量，供图表展示（只读、不分页）；月份为空表示不按完成月份过滤。 */
    @Select("""
        <script>
        SELECT r.sku, r.item_code, r.local_product_name, r.order_count, r.total_quantity,
          (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url))
             FROM attachment a
            WHERE a.source_table='product' AND a.feishu_record_id=r.feishu_record_id
              AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url != '') AS attachment_json
        FROM (
          SELECT s.sku AS sku, MIN(s.item_code) AS item_code, COUNT(DISTINCT s.order_id) AS order_count,
                 SUM(s.quantity) AS total_quantity, p.name AS local_product_name, p.feishu_record_id AS feishu_record_id
          FROM ozon_supply_product_details_20260914 s
          LEFT JOIN product p ON p.id=COALESCE((SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no=s.sku COLLATE utf8mb4_unicode_ci),(SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no=s.item_code COLLATE utf8mb4_unicode_ci))
          <where>
            <if test="shopId != null">s.shop_id = #{shopId}</if>
            <if test="status != null and status != ''">AND s.status = #{status}</if>
            <if test="month != null and month != ''">AND DATE_FORMAT(STR_TO_DATE(s.completion_date, '%d.%m.%Y'), '%Y-%m') = #{month}</if>
          </where>
          GROUP BY s.sku, p.name, p.feishu_record_id
        ) r
        ORDER BY r.total_quantity DESC
        </script>
        """)
    List<OzonSupplyStatsVo> selectProductStats(@Param("shopId") Long shopId, @Param("status") String status,
                                               @Param("month") String month);

    /** 交货图表下钻：某个卖家货号（或某个月份）下的交货申请明细行；店铺 / 状态 / 月份 / 货号为空表示不过滤。 */
    @Select("""
        <script>
        SELECT r.* FROM (
          SELECT s.*, p.name AS local_product_name, (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='product' AND a.feishu_record_id=p.feishu_record_id AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url != '') AS attachment_json
          FROM ozon_supply_product_details_20260914 s
          LEFT JOIN product p ON p.id=COALESCE((SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no=s.sku COLLATE utf8mb4_unicode_ci),(SELECT MIN(p2.id) FROM product p2 WHERE p2.article_no=s.item_code COLLATE utf8mb4_unicode_ci))
        ) r
        <where>
          <if test="sku != null and sku != ''">r.sku = #{sku}</if>
          <if test="shopId != null">AND r.shop_id = #{shopId}</if>
          <if test="status != null and status != ''">AND r.status = #{status}</if>
          <if test="month != null and month != ''">AND DATE_FORMAT(STR_TO_DATE(r.completion_date, '%d.%m.%Y'), '%Y-%m') = #{month}</if>
        </where>
        ORDER BY STR_TO_DATE(r.completion_date, '%d.%m.%Y') DESC, r.id DESC
        </script>
        """)
    List<OzonSupplyReportVo> selectProductRows(@Param("shopId") Long shopId, @Param("status") String status,
                                               @Param("sku") String sku, @Param("month") String month);
}
