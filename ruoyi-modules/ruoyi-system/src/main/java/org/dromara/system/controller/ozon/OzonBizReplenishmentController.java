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
import org.dromara.system.domain.vo.OzonBizReplenishmentVo;
import org.dromara.system.service.IOzonBizReplenishmentService;
/** 补货管理接口。 */
@RestController @Validated @RequiredArgsConstructor
@RequestMapping("/ozon/business/replenishment")
public class OzonBizReplenishmentController extends BaseController {
 private final IOzonBizReplenishmentService service;
 @GetMapping("/list") @SaCheckPermission("ozon:replenishment:list")
 public R<PageResult<OzonBizReplenishmentVo>> list(@Validated OzonBusinessQuery q,PageQuery p) { return R.ok(service.queryPageList(q,p)); }
 @GetMapping("/{id}") @SaCheckPermission("ozon:replenishment:query")
 public R<OzonBizReplenishmentVo> detail(@PathVariable Long id) { return R.ok(service.queryById(id)); }
 @PostMapping @RepeatSubmit @SaCheckPermission("ozon:replenishment:add") @Log(title="补货",businessType=BusinessType.INSERT)
 public R<Long> add(@Validated(AddGroup.class) @RequestBody OzonBizReplenishmentBo bo) { return R.ok(service.insertByBo(bo)); }
 @PutMapping @RepeatSubmit @SaCheckPermission("ozon:replenishment:edit") @Log(title="补货",businessType=BusinessType.UPDATE)
 public R<Void> edit(@Validated(EditGroup.class) @RequestBody OzonBizReplenishmentBo bo) { return toAjax(service.updateByBo(bo)); }
 @DeleteMapping("/{id}") @SaCheckPermission("ozon:replenishment:remove") @Log(title="补货",businessType=BusinessType.DELETE)
 public R<Void> remove(@PathVariable Long id,@RequestParam String revision) { return toAjax(service.deleteWithValidById(id,revision)); }
}
