package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizLogisticsProvider;
import org.dromara.system.domain.vo.OzonBizLogisticsProviderVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/** 物流商资料关联查询。 */
public interface OzonBizLogisticsProviderMapper extends BaseMapperPlus<OzonBizLogisticsProvider,OzonBizLogisticsProviderVo> {
@Select("SELECT (SELECT JSON_ARRAYAGG(JSON_OBJECT('id',a.id,'fileName',a.file_name,'mimeType',a.mime_type,'cosUrl',a.cos_url)) FROM attachment a WHERE a.source_table='logistics_provider' AND a.feishu_record_id=b.feishu_record_id AND a.field_name='跨境运费' AND a.cos_url IS NOT NULL AND a.cos_url<>'') AS attachmentJson,b.* FROM logistics_provider b  ${ew.customSqlSegment}") Page<OzonBizLogisticsProviderVo> selectBusinessPage(Page<OzonBizLogisticsProviderVo> page,@Param("ew") Wrapper<OzonBizLogisticsProvider> wrapper);
}
