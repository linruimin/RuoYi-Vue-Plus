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
import org.dromara.system.domain.vo.OzonBizShopVo;
import org.dromara.system.service.IOzonBizShopService;
/** 店铺管理接口。 */
@RestController @Validated @RequiredArgsConstructor
@RequestMapping("/ozon/business/shop")
public class OzonBizShopController extends BaseController {
 private final IOzonBizShopService service;
 @GetMapping("/list") @SaCheckPermission("ozon:shop:list")
 public R<PageResult<OzonBizShopVo>> list(@Validated OzonBusinessQuery q,PageQuery p) { return R.ok(service.queryPageList(q,p)); }
 @GetMapping("/{id}") @SaCheckPermission("ozon:shop:query")
 public R<OzonBizShopVo> detail(@PathVariable Long id) { return R.ok(service.queryById(id)); }
 @PostMapping @RepeatSubmit @SaCheckPermission("ozon:shop:add") @Log(title="店铺",businessType=BusinessType.INSERT)
 public R<Long> add(@Validated(AddGroup.class) @RequestBody OzonBizShopBo bo) { return R.ok(service.insertByBo(bo)); }
 @PutMapping @RepeatSubmit @SaCheckPermission("ozon:shop:edit") @Log(title="店铺",businessType=BusinessType.UPDATE)
 public R<Void> edit(@Validated(EditGroup.class) @RequestBody OzonBizShopBo bo) { return toAjax(service.updateByBo(bo)); }
 @DeleteMapping("/{id}") @SaCheckPermission("ozon:shop:remove") @Log(title="店铺",businessType=BusinessType.DELETE)
 public R<Void> remove(@PathVariable Long id,@RequestParam String revision) { return toAjax(service.deleteWithValidById(id,revision)); }
}
