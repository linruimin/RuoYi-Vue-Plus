package org.dromara.system.controller.ozon;

import cn.dev33.satoken.stp.StpUtil;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.redis.annotation.RepeatSubmit;
import org.dromara.system.domain.OzonBusinessCustomField;
import org.dromara.system.service.impl.OzonBusinessCustomFieldService;
import org.springframework.web.bind.annotation.*;

/** Create and edit columns backed by persistent per-row values. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ozon/business/custom-fields")
public class OzonBusinessCustomFieldController {
    private final OzonBusinessCustomFieldService service;

    private record Table(String key, String permission) {}
    private record NewField(String label, String type) {}
    private record CellValue(String value) {}

    private Table table(String endpoint) {
        return switch (endpoint) {
            case "product", "replenishment", "shipment", "shop", "attachment" -> new Table(endpoint, endpoint);
            case "purchase-order" -> new Table("purchase_order", "purchaseOrder");
            case "logistics-fee" -> new Table("logistics_fee", "logisticsFee");
            case "other-fee" -> new Table("other_fee", "otherFee");
            case "logistics-provider" -> new Table("logistics_provider", "logisticsProvider");
            case "studio-receipt" -> new Table("studio_receipt", "studioReceipt");
            case "payment-receipt" -> new Table("payment_receipt", "paymentReceipt");
            case "returns" -> new Table("returns", "returns");
            default -> throw new IllegalArgumentException("不支持的业务表");
        };
    }

    @GetMapping("/{endpoint}")
    public R<List<OzonBusinessCustomField>> list(@PathVariable String endpoint) {
        Table t = table(endpoint);
        StpUtil.checkPermission("ozon:" + t.permission() + ":list");
        return R.ok(service.list(t.key()));
    }

    @PostMapping("/{endpoint}/values")
    public R<List<Map<String, Object>>> values(@PathVariable String endpoint, @RequestBody List<Long> ids) {
        Table t = table(endpoint);
        StpUtil.checkPermission("ozon:" + t.permission() + ":list");
        return R.ok(service.values(t.key(), ids));
    }

    @PostMapping("/{endpoint}")
    @RepeatSubmit
    @Log(title = "业务自定义字段", businessType = BusinessType.INSERT)
    public R<OzonBusinessCustomField> add(@PathVariable String endpoint, @RequestBody NewField request) {
        Table t = table(endpoint);
        StpUtil.checkPermission("ozon:" + t.permission() + ":add");
        return R.ok(service.add(t.key(), request.label(), request.type()));
    }

    @PutMapping("/{endpoint}/{fieldId}/rows/{rowId}")
    @Log(title = "业务自定义字段值", businessType = BusinessType.UPDATE)
    public R<Void> save(@PathVariable String endpoint, @PathVariable Long fieldId, @PathVariable Long rowId, @RequestBody CellValue request) {
        Table t = table(endpoint);
        StpUtil.checkPermission("ozon:" + t.permission() + ":edit");
        service.save(t.key(), fieldId, rowId, request.value());
        return R.ok();
    }

    @DeleteMapping("/{endpoint}/{fieldId}")
    @RepeatSubmit
    @Log(title = "业务自定义字段", businessType = BusinessType.DELETE)
    public R<Void> remove(@PathVariable String endpoint, @PathVariable Long fieldId) {
        Table t = table(endpoint);
        StpUtil.checkPermission("ozon:" + t.permission() + ":remove");
        service.remove(t.key(), fieldId);
        return R.ok();
    }
}
