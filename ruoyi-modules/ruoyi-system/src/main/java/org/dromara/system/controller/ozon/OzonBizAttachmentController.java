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
import org.dromara.system.domain.vo.OzonBizAttachmentVo;
import org.dromara.system.domain.vo.OzonAttachmentUploadVo;
import org.dromara.system.service.IOzonBizAttachmentService;
import org.springframework.web.multipart.MultipartFile;
/** 附件管理接口。 */
@RestController @Validated @RequiredArgsConstructor
@RequestMapping("/ozon/business/attachment")
public class OzonBizAttachmentController extends BaseController {
 private final IOzonBizAttachmentService service;
 @GetMapping("/list") @SaCheckPermission("ozon:attachment:list")
 public R<PageResult<OzonBizAttachmentVo>> list(@Validated OzonBusinessQuery q,PageQuery p) { return R.ok(service.queryPageList(q,p)); }
 @GetMapping("/{id}") @SaCheckPermission("ozon:attachment:query")
 public R<OzonBizAttachmentVo> detail(@PathVariable Long id) { return R.ok(service.queryById(id)); }
 @PostMapping @RepeatSubmit @SaCheckPermission("ozon:attachment:add") @Log(title="附件",businessType=BusinessType.INSERT)
 public R<Long> add(@Validated(AddGroup.class) @RequestBody OzonBizAttachmentBo bo) { return R.ok(service.insertByBo(bo)); }
 @PostMapping("/upload") @SaCheckPermission("ozon:attachment:add") @Log(title="附件",businessType=BusinessType.INSERT)
 public R<OzonAttachmentUploadVo> upload(@RequestPart("file") MultipartFile file,@RequestParam String sourceTable,@RequestParam String fieldName,@RequestParam(required=false) String fileName) { return R.ok(service.upload(sourceTable,fieldName,fileName,file)); }
 @PutMapping @RepeatSubmit @SaCheckPermission("ozon:attachment:edit") @Log(title="附件",businessType=BusinessType.UPDATE)
 public R<Void> edit(@Validated(EditGroup.class) @RequestBody OzonBizAttachmentBo bo) { return toAjax(service.updateByBo(bo)); }
 @DeleteMapping("/{id}") @SaCheckPermission("ozon:attachment:remove") @Log(title="附件",businessType=BusinessType.DELETE)
 public R<Void> remove(@PathVariable Long id,@RequestParam String revision) { return toAjax(service.deleteWithValidById(id,revision)); }
}
