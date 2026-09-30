package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizLogisticsFee;
import org.dromara.system.domain.vo.OzonBizLogisticsFeeVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/** 物流费用关联查询。 */
public interface OzonBizLogisticsFeeMapper extends BaseMapperPlus<OzonBizLogisticsFee,OzonBizLogisticsFeeVo> {
@Select("SELECT (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='logistics_fee' AND a.feishu_record_id=b.feishu_record_id AND a.field_name='附件' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachmentJson,b.*,v.`总重量` AS calc0,v.`总体积` AS calc1,v.`单体积费用` AS calc2,v.`单重量费用` AS calc3,r_shop_id.`name` AS shopIdLabel FROM logistics_fee b LEFT JOIN v_logistics_fee v ON v.id=b.id LEFT JOIN shop r_shop_id ON r_shop_id.id=b.`shop_id` ${ew.customSqlSegment}") Page<OzonBizLogisticsFeeVo> selectBusinessPage(Page<OzonBizLogisticsFeeVo> page,@Param("ew") Wrapper<OzonBizLogisticsFee> wrapper);
}
