package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizAttachment;
import org.dromara.system.domain.vo.OzonBizAttachmentVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.*;
/** 附件关联查询。 */
public interface OzonBizAttachmentMapper extends BaseMapperPlus<OzonBizAttachment,OzonBizAttachmentVo> {
@Select("SELECT JSON_ARRAY(JSON_OBJECT('id',b.id,'fileName',b.file_name,'mimeType',b.mime_type,'cosUrl',b.cos_url)) AS attachmentJson,b.* FROM attachment b  ${ew.customSqlSegment}") Page<OzonBizAttachmentVo> selectBusinessPage(Page<OzonBizAttachmentVo> page,@Param("ew") Wrapper<OzonBizAttachment> wrapper);
/** 某条业务记录某个附件字段已登记的附件，供保存时比对增删。 */
@Select("SELECT id,file_name,cos_key FROM attachment WHERE source_table=#{table} AND feishu_record_id=#{source} AND field_name=#{field} ORDER BY id") List<Map<String,Object>> listRefs(@Param("table") String table,@Param("source") String source,@Param("field") String field);
}
