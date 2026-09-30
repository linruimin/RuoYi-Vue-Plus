package org.dromara.system.controller.ozon;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.dromara.common.core.domain.R;
import org.dromara.system.service.impl.OzonBusinessRowPositionService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
/** 表格手动行位置。 */
@RestController @RequiredArgsConstructor @RequestMapping("/ozon/business/row-position")
public class OzonBusinessRowPositionController {
 private final OzonBusinessRowPositionService service;
 public record PositionRequest(@NotNull Long rowId,@NotNull Long anchorId,@NotNull String placement){}
 @PostMapping("/{table}")
 public R<Void> place(@PathVariable String table,@Valid @RequestBody PositionRequest request){
  String key=table.replace('-','_');
  String permission=switch(key){case "purchase_order" -> "purchaseOrder";case "logistics_fee" -> "logisticsFee";case "other_fee" -> "otherFee";case "logistics_provider" -> "logisticsProvider";case "studio_receipt" -> "studioReceipt";case "payment_receipt" -> "paymentReceipt";default -> key;};
  StpUtil.checkPermission("ozon:"+permission+":add");
  service.place(key,request.rowId(),request.anchorId(),request.placement());
  return R.ok();
 }
}
