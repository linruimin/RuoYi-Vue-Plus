package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizReplenishment;
import org.dromara.system.domain.vo.OzonBizReplenishmentVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/** 补货关联查询。 */
public interface OzonBizReplenishmentMapper extends BaseMapperPlus<OzonBizReplenishment,OzonBizReplenishmentVo> {
@Select("SELECT (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='product' AND a.feishu_record_id=r_product_id.feishu_record_id AND a.field_name='货品图片' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachmentJson,b.*,r_product_id.`name` AS productIdLabel FROM replenishment b LEFT JOIN product r_product_id ON r_product_id.id=b.`product_id` ${ew.customSqlSegment}") Page<OzonBizReplenishmentVo> selectBusinessPage(Page<OzonBizReplenishmentVo> page,@Param("ew") Wrapper<OzonBizReplenishment> wrapper);
}
