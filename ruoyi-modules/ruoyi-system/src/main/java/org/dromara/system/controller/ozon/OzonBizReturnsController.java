package org.dromara.system.controller.ozon;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.common.core.domain.*;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.OzonBusinessQuery;
import org.dromara.system.domain.vo.OzonBizReturnsVo;
import org.dromara.system.service.IOzonBizReturnsService;
/** 退货管理接口（只读）。 */
@RestController @Validated @RequiredArgsConstructor
@RequestMapping("/ozon/business/returns")
public class OzonBizReturnsController extends BaseController {
 private final IOzonBizReturnsService service;
 @GetMapping("/list") @SaCheckPermission("ozon:returns:list")
 public R<PageResult<OzonBizReturnsVo>> list(@Validated OzonBusinessQuery q,PageQuery p) { return R.ok(service.queryPageList(q,p)); }
 @GetMapping("/{id}") @SaCheckPermission("ozon:returns:query")
 public R<OzonBizReturnsVo> detail(@PathVariable Long id) { return R.ok(service.queryById(id)); }
}
