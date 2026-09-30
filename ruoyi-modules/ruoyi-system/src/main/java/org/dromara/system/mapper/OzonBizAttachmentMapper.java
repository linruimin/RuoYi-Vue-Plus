package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizAttachment;
import org.dromara.system.domain.vo.OzonBizAttachmentVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/** 附件关联查询。 */
public interface OzonBizAttachmentMapper extends BaseMapperPlus<OzonBizAttachment,OzonBizAttachmentVo> {
@Select("SELECT JSON_ARRAY(JSON_OBJECT('id',b.id,'fileName',b.file_name,'mimeType',b.mime_type,'cosUrl',b.cos_url)) AS attachmentJson,b.* FROM attachment b  ${ew.customSqlSegment}") Page<OzonBizAttachmentVo> selectBusinessPage(Page<OzonBizAttachmentVo> page,@Param("ew") Wrapper<OzonBizAttachment> wrapper);
}
