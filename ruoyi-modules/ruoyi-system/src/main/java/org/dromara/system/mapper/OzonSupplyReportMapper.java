package org.dromara.system.mapper;

import org.dromara.system.domain.OzonSupplyReport;
import org.dromara.system.domain.vo.OzonSupplyReportVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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
}
