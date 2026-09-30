package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizShipment;
import org.dromara.system.domain.vo.OzonBizShipmentVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/** 出货关联查询。 */
public interface OzonBizShipmentMapper extends BaseMapperPlus<OzonBizShipment,OzonBizShipmentVo> {
@Select("SELECT (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='product' AND a.feishu_record_id=p_view.feishu_record_id AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachmentJson,b.*,p_view.product_no AS productNo,p_view.name AS productName,p_view.backend_price AS backendPrice,r_purchase_id.paid_at AS orderPaidAt,po_view.`装箱总数` AS orderPackedQty,v.`单箱体积/m3` AS calc0,v.`总数` AS calc1,v.`总箱重/kg` AS calc2,v.`单箱跨境运输费用` AS calc3,v.`单个跨境运输费用` AS calc4,v.`单个打包费用` AS calc5,v.`单个店铺费用+购买费用` AS calc6,v.`单个取货送仓费用` AS calc7,v.`每包成本` AS calc8,v.`预计到手人民币` AS calc9,v.`单体积入仓送仓费用` AS calc10,v.`单箱入仓送仓费用` AS calc11,v.`单个入仓送仓费用` AS calc12,v.`单箱跨境运输+入仓送仓费用` AS calc13,v.`合计成本` AS calc14,v.`密度` AS calc15,v.`总箱体积/m3` AS calc16,v.`总合计成本` AS calc17,v.`预计利润` AS calc18,v.`预计倍数` AS calc19,r_purchase_id.`order_no` AS purchaseIdLabel,r_shop_id.`name` AS shopIdLabel FROM shipment b LEFT JOIN v_app_shipment v ON v.id=b.id LEFT JOIN purchase_order r_purchase_id ON r_purchase_id.id=b.`purchase_id` LEFT JOIN shop r_shop_id ON r_shop_id.id=b.`shop_id` LEFT JOIN product p_view ON p_view.id=r_purchase_id.product_id LEFT JOIN v_app_purchase_order po_view ON po_view.id=r_purchase_id.id ${ew.customSqlSegment}") Page<OzonBizShipmentVo> selectBusinessPage(Page<OzonBizShipmentVo> page,@Param("ew") Wrapper<OzonBizShipment> wrapper);
}
