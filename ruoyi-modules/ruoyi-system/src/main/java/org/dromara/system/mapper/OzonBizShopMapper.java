package org.dromara.system.mapper;
import org.dromara.system.domain.OzonBizShop;
import org.dromara.system.domain.vo.OzonBizShopVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/** 店铺关联查询。 */
public interface OzonBizShopMapper extends BaseMapperPlus<OzonBizShop,OzonBizShopVo> {
@Select("SELECT b.* FROM shop b  ${ew.customSqlSegment}") Page<OzonBizShopVo> selectBusinessPage(Page<OzonBizShopVo> page,@Param("ew") Wrapper<OzonBizShop> wrapper);
}
