package org.dromara.system.controller.ozon;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.system.service.impl.OzonBusinessSupport;
import org.springframework.web.bind.annotation.*;
/** 业务表自动编号预览：新增行时先展示即将分配的编号，最终编号以保存结果为准。 */
@RestController @RequiredArgsConstructor @RequestMapping("/ozon/business/number")
public class OzonBusinessNumberController {
 private final OzonBusinessSupport support;
 @GetMapping("/{table}")
 public R<Long> next(@PathVariable String table){
  String key=table.replace('-','_');
  String permission=switch(key){case "purchase_order" -> "purchaseOrder";case "logistics_fee" -> "logisticsFee";case "other_fee" -> "otherFee";case "logistics_provider" -> "logisticsProvider";case "studio_receipt" -> "studioReceipt";case "payment_receipt" -> "paymentReceipt";default -> key;};
  StpUtil.checkPermission("ozon:"+permission+":add");
  String field=support.numberColumn(key);
  if(field==null)return R.fail("该业务表没有自动编号字段");
  return R.ok(support.nextNumber(key,field));
 }
}
