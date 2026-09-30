package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizPaymentReceipt;
import org.dromara.system.domain.vo.OzonBizPaymentReceiptVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/** 回款关联查询。 */
public interface OzonBizPaymentReceiptMapper extends BaseMapperPlus<OzonBizPaymentReceipt,OzonBizPaymentReceiptVo> {
@Select("SELECT (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='payment_receipt' AND a.feishu_record_id=b.feishu_record_id AND a.field_name='水单' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachmentJson,b.*,r_shop_id.`name` AS shopIdLabel FROM payment_receipt b LEFT JOIN shop r_shop_id ON r_shop_id.id=b.`shop_id` ${ew.customSqlSegment}") Page<OzonBizPaymentReceiptVo> selectBusinessPage(Page<OzonBizPaymentReceiptVo> page,@Param("ew") Wrapper<OzonBizPaymentReceipt> wrapper);
}
