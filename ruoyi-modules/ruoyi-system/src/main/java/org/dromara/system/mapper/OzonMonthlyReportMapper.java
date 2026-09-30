package org.dromara.system.mapper;

import org.dromara.system.domain.OzonMonthlyReport;
import org.dromara.system.domain.vo.OzonMonthlyReportVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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
}
