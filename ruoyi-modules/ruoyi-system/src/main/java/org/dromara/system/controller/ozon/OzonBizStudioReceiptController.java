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
import org.dromara.system.domain.vo.OzonBizStudioReceiptVo;
import org.dromara.system.service.IOzonBizStudioReceiptService;
/** 工作室收货管理接口。 */
@RestController @Validated @RequiredArgsConstructor
@RequestMapping("/ozon/business/studio-receipt")
public class OzonBizStudioReceiptController extends BaseController {
 private final IOzonBizStudioReceiptService service;
 @GetMapping("/list") @SaCheckPermission("ozon:studioReceipt:list")
 public R<PageResult<OzonBizStudioReceiptVo>> list(@Validated OzonBusinessQuery q,PageQuery p) { return R.ok(service.queryPageList(q,p)); }
 @GetMapping("/{id}") @SaCheckPermission("ozon:studioReceipt:query")
 public R<OzonBizStudioReceiptVo> detail(@PathVariable Long id) { return R.ok(service.queryById(id)); }
 @PostMapping @RepeatSubmit @SaCheckPermission("ozon:studioReceipt:add") @Log(title="工作室收货",businessType=BusinessType.INSERT)
 public R<Long> add(@Validated(AddGroup.class) @RequestBody OzonBizStudioReceiptBo bo) { return R.ok(service.insertByBo(bo)); }
 @PutMapping @RepeatSubmit @SaCheckPermission("ozon:studioReceipt:edit") @Log(title="工作室收货",businessType=BusinessType.UPDATE)
 public R<Void> edit(@Validated(EditGroup.class) @RequestBody OzonBizStudioReceiptBo bo) { return toAjax(service.updateByBo(bo)); }
 @DeleteMapping("/{id}") @SaCheckPermission("ozon:studioReceipt:remove") @Log(title="工作室收货",businessType=BusinessType.DELETE)
 public R<Void> remove(@PathVariable Long id,@RequestParam String revision) { return toAjax(service.deleteWithValidById(id,revision)); }
}
