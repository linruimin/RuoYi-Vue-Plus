package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizProduct;
import org.dromara.system.domain.vo.OzonBizProductVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/** 产品关联查询。 */
public interface OzonBizProductMapper extends BaseMapperPlus<OzonBizProduct,OzonBizProductVo> {
@Select("SELECT (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='product' AND a.feishu_record_id=b.feishu_record_id AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachmentJson,b.*,v.`进货装箱总数` AS calc0,v.`出货总数` AS calc1,v.`总合计成本` AS calc2,v.`后台售价总值` AS calc3,v.`预计到手总值` AS calc4,v.`预计到手人民币` AS calc5,v.`预计利润` AS calc6,v.`预计倍数` AS calc7,v.`平均合计成本` AS calc8,r_shop_id.`name` AS shopIdLabel,r_parent_id.`name` AS parentIdLabel FROM product b LEFT JOIN v_app_product v ON v.id=b.id LEFT JOIN shop r_shop_id ON r_shop_id.id=b.`shop_id` LEFT JOIN product r_parent_id ON r_parent_id.id=b.`parent_id` ${ew.customSqlSegment}") Page<OzonBizProductVo> selectBusinessPage(Page<OzonBizProductVo> page,@Param("ew") Wrapper<OzonBizProduct> wrapper);
}
