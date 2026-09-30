package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizStudioReceipt;
import org.dromara.system.domain.vo.OzonBizStudioReceiptVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/** 工作室收货关联查询。 */
public interface OzonBizStudioReceiptMapper extends BaseMapperPlus<OzonBizStudioReceipt,OzonBizStudioReceiptVo> {
@Select("SELECT (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='product' AND a.feishu_record_id=p_view.feishu_record_id AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachmentJson,b.*,p_view.name AS productName,v.`收货每箱数量` AS calc0,v.`箱体积/m3` AS calc1,v.`总箱重/kg` AS calc2 FROM studio_receipt b LEFT JOIN v_app_studio_receipt v ON v.id=b.id LEFT JOIN purchase_order po_view ON po_view.order_no=b.order_no LEFT JOIN product p_view ON p_view.id=po_view.product_id ${ew.customSqlSegment}") Page<OzonBizStudioReceiptVo> selectBusinessPage(Page<OzonBizStudioReceiptVo> page,@Param("ew") Wrapper<OzonBizStudioReceipt> wrapper);
}
