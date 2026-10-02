package org.dromara.system.controller.ozon;
import cn.dev33.satoken.stp.StpUtil;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.system.service.impl.OzonBusinessRemovedFieldService;
import org.springframework.web.bind.annotation.*;
/** 业务字段全局删除，核心结构字段及非空字段受保护。 */
@RestController @RequiredArgsConstructor @RequestMapping("/ozon/business/removed-fields")
public class OzonBusinessRemovedFieldController {
 private final OzonBusinessRemovedFieldService service;
 private record Table(String key,String permission){}
 private Table table(String endpoint){
  return switch(endpoint){
   case "product","replenishment","shipment","shop","attachment" -> new Table(endpoint,endpoint);
   case "purchase-order" -> new Table("purchase_order","purchaseOrder");
   case "logistics-fee" -> new Table("logistics_fee","logisticsFee");
   case "other-fee" -> new Table("other_fee","otherFee");
   case "logistics-provider" -> new Table("logistics_provider","logisticsProvider");
   case "studio-receipt" -> new Table("studio_receipt","studioReceipt");
   case "payment-receipt" -> new Table("payment_receipt","paymentReceipt");
   case "returns" -> new Table("returns","returns");
   default -> throw new IllegalArgumentException("不支持的业务表");
  };
 }
 @GetMapping("/{endpoint}")
 public R<List<String>> list(@PathVariable String endpoint){Table t=table(endpoint);StpUtil.checkPermission("ozon:"+t.permission()+":list");return R.ok(service.list(t.key()));}
 @DeleteMapping("/{endpoint}/{prop}")
 public R<Void> remove(@PathVariable String endpoint,@PathVariable String prop){Table t=table(endpoint);StpUtil.checkPermission("ozon:"+t.permission()+":remove");service.remove(t.key(),prop);return R.ok();}
}
