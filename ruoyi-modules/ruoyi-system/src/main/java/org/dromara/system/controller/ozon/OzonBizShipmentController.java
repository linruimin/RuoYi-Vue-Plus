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
import org.dromara.system.domain.vo.OzonBizShipmentVo;
import org.dromara.system.service.IOzonBizShipmentService;
/** 出货管理接口。 */
@RestController @Validated @RequiredArgsConstructor
@RequestMapping("/ozon/business/shipment")
public class OzonBizShipmentController extends BaseController {
 private final IOzonBizShipmentService service;
 @GetMapping("/list") @SaCheckPermission("ozon:shipment:list")
 public R<PageResult<OzonBizShipmentVo>> list(@Validated OzonBusinessQuery q,PageQuery p) { return R.ok(service.queryPageList(q,p)); }
 @GetMapping("/{id}") @SaCheckPermission("ozon:shipment:query")
 public R<OzonBizShipmentVo> detail(@PathVariable Long id) { return R.ok(service.queryById(id)); }
 @PostMapping @RepeatSubmit @SaCheckPermission("ozon:shipment:add") @Log(title="出货",businessType=BusinessType.INSERT)
 public R<Long> add(@Validated(AddGroup.class) @RequestBody OzonBizShipmentBo bo) { return R.ok(service.insertByBo(bo)); }
 @PutMapping @RepeatSubmit @SaCheckPermission("ozon:shipment:edit") @Log(title="出货",businessType=BusinessType.UPDATE)
 public R<Void> edit(@Validated(EditGroup.class) @RequestBody OzonBizShipmentBo bo) { return toAjax(service.updateByBo(bo)); }
 @DeleteMapping("/{id}") @SaCheckPermission("ozon:shipment:remove") @Log(title="出货",businessType=BusinessType.DELETE)
 public R<Void> remove(@PathVariable Long id,@RequestParam String revision) { return toAjax(service.deleteWithValidById(id,revision)); }
}
