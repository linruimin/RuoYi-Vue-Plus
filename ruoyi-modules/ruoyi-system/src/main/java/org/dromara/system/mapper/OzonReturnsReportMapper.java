package org.dromara.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.OzonBizReturns;
import org.dromara.system.domain.vo.OzonReturnsReportVo;

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
}
