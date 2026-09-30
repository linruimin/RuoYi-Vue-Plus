package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizPurchaseOrder;
import org.dromara.system.domain.vo.OzonBizPurchaseOrderVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/** 进货关联查询。 */
public interface OzonBizPurchaseOrderMapper extends BaseMapperPlus<OzonBizPurchaseOrder,OzonBizPurchaseOrderVo> {
@Select("SELECT (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='product' AND a.feishu_record_id=r_product_id.feishu_record_id AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachmentJson,b.*,v.`每包数量` AS calc0,v.`单个成本` AS calc1,v.`装箱总数` AS calc2,v.`每包成本` AS calc3,r_product_id.`name` AS productIdLabel,r_shop_id.`name` AS shopIdLabel FROM purchase_order b LEFT JOIN v_app_purchase_order v ON v.id=b.id LEFT JOIN product r_product_id ON r_product_id.id=b.`product_id` LEFT JOIN shop r_shop_id ON r_shop_id.id=b.`shop_id` ${ew.customSqlSegment}") Page<OzonBizPurchaseOrderVo> selectBusinessPage(Page<OzonBizPurchaseOrderVo> page,@Param("ew") Wrapper<OzonBizPurchaseOrder> wrapper);
}
