package org.dromara.system.controller.ozon;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.OzonOpsQuery;
import org.dromara.system.domain.vo.OzonOpsDailyVo;
import org.dromara.system.domain.vo.OzonOpsDetailVo;
import org.dromara.system.service.IOzonOpsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 「运营 / 1.运营情况」接口。
 * <p>
 * 数据源 {@code ozon_ops_daily}（我们自己算的每日运营简报，脚本 {@code scripts/ozon_ops_daily_report.py}）。
 * 与其它 Ozon 页面不同，这里拿的是**已经聚合好的结论**，不再做维度筛选：
 * 一天一条，双店合并成一份，没有 shop_id 维度，所以也没有 scopeShopId 参数。
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ozon/ops")
public class OzonOpsController extends BaseController {

    private final IOzonOpsService opsService;

    /** 简报日期列表（按日期倒序），列表页只取摘要字段，不含正文。 */
    @SaCheckPermission("ozon:ops:list")
    @GetMapping("/list")
    public R<List<OzonOpsDailyVo>> list(OzonOpsQuery query) {
        return R.ok(opsService.list(query));
    }

    /** 单日简报全文（正文以预渲染 HTML 返回，前端直接渲染）。 */
    @SaCheckPermission("ozon:ops:list")
    @GetMapping("/detail")
    public R<OzonOpsDetailVo> detail(@RequestParam Long id) {
        return R.ok(opsService.detail(id));
    }
}
