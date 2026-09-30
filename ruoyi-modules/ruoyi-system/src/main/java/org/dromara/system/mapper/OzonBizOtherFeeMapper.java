package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizOtherFee;
import org.dromara.system.domain.vo.OzonBizOtherFeeVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/** 其他费用关联查询。 */
public interface OzonBizOtherFeeMapper extends BaseMapperPlus<OzonBizOtherFee,OzonBizOtherFeeVo> {
@Select("SELECT (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='other_fee' AND a.feishu_record_id=b.feishu_record_id AND a.field_name='附件' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachmentJson,b.*,r_shop_id.`name` AS shopIdLabel FROM other_fee b LEFT JOIN shop r_shop_id ON r_shop_id.id=b.`shop_id` ${ew.customSqlSegment}") Page<OzonBizOtherFeeVo> selectBusinessPage(Page<OzonBizOtherFeeVo> page,@Param("ew") Wrapper<OzonBizOtherFee> wrapper);
}
