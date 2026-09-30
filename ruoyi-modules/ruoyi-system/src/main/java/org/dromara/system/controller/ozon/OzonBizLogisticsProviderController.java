package org.dromara.system.controller.ozon;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.common.core.domain.*;
import org.dromara.common.core.validate.*;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.redis.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.OzonBizLogisticsProviderVo;
import org.dromara.system.service.IOzonBizLogisticsProviderService;
/** 物流商资料管理接口。 */
@RestController @Validated @RequiredArgsConstructor
@RequestMapping("/ozon/business/logistics-provider")
public class OzonBizLogisticsProviderController extends BaseController {
 private final IOzonBizLogisticsProviderService service;
 @GetMapping("/list") @SaCheckPermission("ozon:logisticsProvider:list")
 public R<PageResult<OzonBizLogisticsProviderVo>> list(@Validated OzonBusinessQuery q,PageQuery p) { return R.ok(service.queryPageList(q,p)); }
 @GetMapping("/{id}") @SaCheckPermission("ozon:logisticsProvider:query")
 public R<OzonBizLogisticsProviderVo> detail(@PathVariable Long id) { return R.ok(service.queryById(id)); }
 @PostMapping @RepeatSubmit @SaCheckPermission("ozon:logisticsProvider:add") @Log(title="物流商资料",businessType=BusinessType.INSERT)
 public R<Long> add(@Validated(AddGroup.class) @RequestBody OzonBizLogisticsProviderBo bo) { return R.ok(service.insertByBo(bo)); }
 @PutMapping @RepeatSubmit @SaCheckPermission("ozon:logisticsProvider:edit") @Log(title="物流商资料",businessType=BusinessType.UPDATE)
 public R<Void> edit(@Validated(EditGroup.class) @RequestBody OzonBizLogisticsProviderBo bo) { return toAjax(service.updateByBo(bo)); }
 @DeleteMapping("/{id}") @SaCheckPermission("ozon:logisticsProvider:remove") @Log(title="物流商资料",businessType=BusinessType.DELETE)
 public R<Void> remove(@PathVariable Long id,@RequestParam String revision) { return toAjax(service.deleteWithValidById(id,revision)); }
}
