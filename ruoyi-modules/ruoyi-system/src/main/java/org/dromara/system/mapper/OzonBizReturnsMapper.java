package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizReturns;
import org.dromara.system.domain.vo.OzonBizReturnsVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/** 退货只读查询：关联产品库取图片/编号/品名，关联店铺取名称。 */
public interface OzonBizReturnsMapper extends BaseMapperPlus<OzonBizReturns,OzonBizReturnsVo> {
@Select("SELECT b.*,p_view.id AS productId,p_view.product_no AS productNo,p_view.name AS productName,r_shop_id.`name` AS shopIdLabel,(SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='product' AND a.feishu_record_id=p_view.feishu_record_id AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachmentJson FROM `returns` b LEFT JOIN product p_view ON p_view.article_no=b.article_no LEFT JOIN shop r_shop_id ON r_shop_id.id=b.shop_id ${ew.customSqlSegment}")
Page<OzonBizReturnsVo> selectBusinessPage(Page<OzonBizReturnsVo> page,@Param("ew") Wrapper<OzonBizReturns> wrapper);
}
