package org.dromara.system.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import tools.jackson.core.type.TypeReference;
import org.dromara.system.domain.bo.OzonBusinessQuery;
import org.dromara.system.mapper.OzonBusinessRelationMapper;
import org.dromara.system.mapper.OzonBusinessRemovedFieldMapper;
import org.springframework.beans.BeanWrapperImpl;
import java.util.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
/** 业务表共用的字段白名单、关联完整性和并发检查。 */
@Component @RequiredArgsConstructor @DS("ozon")
public class OzonBusinessSupport {
 private final OzonBusinessRelationMapper mapper;
 private final OzonBusinessRemovedFieldMapper removedMapper;
 private record Link(String table,String column,String target) {}
 private static final Map<String,Map<String,String>> columns=new HashMap<>();
 private static final Map<String,Set<String>> numeric=new HashMap<>(),dates=new HashMap<>(),options=new HashMap<>();
 private static final Map<String,String> auto=new HashMap<>();
 private static final List<Link> links=new ArrayList<>();
 static {
columns.put("product",Map.ofEntries(Map.entry("id","b.`id`"),Map.entry("productNo","b.`product_no`"),Map.entry("sku","b.`sku`"),Map.entry("name","b.`name`"),Map.entry("articleNo","b.`article_no`"),Map.entry("shopId","b.`shop_id`"),Map.entry("sellerCompany","b.`seller_company`"),Map.entry("unitPerPack","b.`unit_per_pack`"),Map.entry("backendPrice","b.`backend_price`"),Map.entry("buyerPay","b.`buyer_pay`"),Map.entry("receivedPrice","b.`received_price`"),Map.entry("greenPrice","b.`green_price`"),Map.entry("grayPrice","b.`gray_price`"),Map.entry("strikePrice","b.`strike_price`"),Map.entry("parentId","b.`parent_id`"),Map.entry("remark","b.`remark`"),Map.entry("createdAt","b.`created_at`"),Map.entry("updatedAt","b.`updated_at`"),Map.entry("calc0","v.`进货装箱总数`"),Map.entry("calc1","v.`出货总数`"),Map.entry("calc2","v.`总合计成本`"),Map.entry("calc3","v.`后台售价总值`"),Map.entry("calc4","v.`预计到手总值`"),Map.entry("calc5","v.`预计到手人民币`"),Map.entry("calc6","v.`预计利润`"),Map.entry("calc7","v.`预计倍数`"),Map.entry("calc8","v.`平均合计成本`")));
numeric.put("product",Set.of("id","productNo","shopId","unitPerPack","backendPrice","buyerPay","receivedPrice","greenPrice","grayPrice","strikePrice","parentId","calc0","calc1","calc2","calc3","calc4","calc5","calc6","calc7","calc8"));
dates.put("product",Set.of("createdAt","updatedAt"));
links.add(new Link("product","shop_id","shop"));
links.add(new Link("product","parent_id","product"));
columns.put("replenishment",Map.ofEntries(Map.entry("id","b.`id`"),Map.entry("replenishNo","b.`replenish_no`"),Map.entry("productId","b.`product_id`"),Map.entry("expectedDate","b.`expected_date`"),Map.entry("expectedQty","b.`expected_qty`"),Map.entry("method","b.`method`"),Map.entry("remark","b.`remark`"),Map.entry("createdAt","b.`created_at`"),Map.entry("updatedAt","b.`updated_at`")));
numeric.put("replenishment",Set.of("id","replenishNo","productId","expectedQty"));
dates.put("replenishment",Set.of("expectedDate","createdAt","updatedAt"));
options.put("replenishment.method",Set.of("跨境","立德"));
links.add(new Link("replenishment","product_id","product"));
columns.put("purchase_order",Map.ofEntries(Map.entry("id","b.`id`"),Map.entry("orderNo","b.`order_no`"),Map.entry("productId","b.`product_id`"),Map.entry("shopId","b.`shop_id`"),Map.entry("actualPaid","b.`actual_paid`"),Map.entry("quantity","b.`quantity`"),Map.entry("payStatus","b.`pay_status`"),Map.entry("channel","b.`channel`"),Map.entry("paidAt","b.`paid_at`"),Map.entry("remark","b.`remark`"),Map.entry("createdAt","b.`created_at`"),Map.entry("updatedAt","b.`updated_at`"),Map.entry("calc0","v.`每包数量`"),Map.entry("calc1","v.`单个成本`"),Map.entry("calc2","v.`装箱总数`"),Map.entry("calc3","v.`每包成本`")));
numeric.put("purchase_order",Set.of("id","productId","shopId","actualPaid","quantity","calc0","calc1","calc2","calc3"));
dates.put("purchase_order",Set.of("paidAt","createdAt","updatedAt"));
options.put("purchase_order.payStatus",Set.of("已付","未付"));
options.put("purchase_order.channel",Set.of("自己","林灼"));
links.add(new Link("purchase_order","product_id","product"));
links.add(new Link("purchase_order","shop_id","shop"));
columns.put("shipment",Map.ofEntries(Map.entry("id","b.`id`"),Map.entry("shipmentNo","b.`shipment_no`"),Map.entry("purchaseId","b.`purchase_id`"),Map.entry("shopId","b.`shop_id`"),Map.entry("logisticsMethod","b.`logistics_method`"),Map.entry("goodsStatus","b.`goods_status`"),Map.entry("boxSpec","b.`box_spec`"),Map.entry("boxes","b.`boxes`"),Map.entry("perBoxQty","b.`per_box_qty`"),Map.entry("boxWeight","b.`box_weight`"),Map.entry("warehouseInNo","b.`warehouse_in_no`"),Map.entry("shippingMark","b.`shipping_mark`"),Map.entry("perBoxPackingFee","b.`per_box_packing_fee`"),Map.entry("perBoxShopBuyFee","b.`per_box_shop_buy_fee`"),Map.entry("perBoxPickupFee","b.`per_box_pickup_fee`"),Map.entry("crossRateUsdPerKg","b.`cross_rate_usd_per_kg`"),Map.entry("shippedAt","b.`shipped_at`"),Map.entry("warehouseInAt","b.`warehouse_in_at`"),Map.entry("createdAt","b.`created_at`"),Map.entry("updatedAt","b.`updated_at`"),Map.entry("calc0","v.`单箱体积/m3`"),Map.entry("calc1","v.`总数`"),Map.entry("calc2","v.`总箱重/kg`"),Map.entry("calc3","v.`单箱跨境运输费用`"),Map.entry("calc4","v.`单个跨境运输费用`"),Map.entry("calc5","v.`单个打包费用`"),Map.entry("calc6","v.`单个店铺费用+购买费用`"),Map.entry("calc7","v.`单个取货送仓费用`"),Map.entry("calc8","v.`每包成本`"),Map.entry("calc9","v.`预计到手人民币`"),Map.entry("calc10","v.`单体积入仓送仓费用`"),Map.entry("calc11","v.`单箱入仓送仓费用`"),Map.entry("calc12","v.`单个入仓送仓费用`"),Map.entry("calc13","v.`单箱跨境运输+入仓送仓费用`"),Map.entry("calc14","v.`合计成本`"),Map.entry("calc15","v.`密度`"),Map.entry("calc16","v.`总箱体积/m3`"),Map.entry("calc17","v.`总合计成本`"),Map.entry("calc18","v.`预计利润`"),Map.entry("calc19","v.`预计倍数`")));
numeric.put("shipment",Set.of("id","shipmentNo","purchaseId","shopId","boxes","perBoxQty","boxWeight","perBoxPackingFee","perBoxShopBuyFee","perBoxPickupFee","crossRateUsdPerKg","calc0","calc1","calc2","calc3","calc4","calc5","calc6","calc7","calc8","calc9","calc10","calc11","calc12","calc13","calc14","calc15","calc16","calc17","calc18","calc19"));
dates.put("shipment",Set.of("shippedAt","warehouseInAt","createdAt","updatedAt"));
options.put("shipment.logisticsMethod",Set.of("立德","超光速","贝加尔","跨境店","边境带货"));
options.put("shipment.goodsStatus",Set.of("商家仓库","林灼工作室","工作室仓库","国内运输","物流仓库","跨境运输","俄罗斯仓库","ozon官方仓"));
links.add(new Link("shipment","purchase_id","purchase_order"));
links.add(new Link("shipment","shop_id","shop"));
columns.put("logistics_fee",Map.ofEntries(Map.entry("id","b.`id`"),Map.entry("feeNo","b.`fee_no`"),Map.entry("feeType","b.`fee_type`"),Map.entry("amount","b.`amount`"),Map.entry("paidFlag","b.`paid_flag`"),Map.entry("feeDate","b.`fee_date`"),Map.entry("shopId","b.`shop_id`"),Map.entry("remark","b.`remark`"),Map.entry("createdAt","b.`created_at`"),Map.entry("updatedAt","b.`updated_at`"),Map.entry("calc0","v.`总重量`"),Map.entry("calc1","v.`总体积`"),Map.entry("calc2","v.`单体积费用`"),Map.entry("calc3","v.`单重量费用`")));
numeric.put("logistics_fee",Set.of("id","feeNo","amount","shopId","calc0","calc1","calc2","calc3"));
dates.put("logistics_fee",Set.of("feeDate","createdAt","updatedAt"));
options.put("logistics_fee.feeType",Set.of("国内运输","跨境运输","入仓送仓","跨境取货送仓","边境带货"));
options.put("logistics_fee.paidFlag",Set.of("已付","未付"));
links.add(new Link("logistics_fee","shop_id","shop"));
columns.put("other_fee",Map.ofEntries(Map.entry("id","b.`id`"),Map.entry("feeNo","b.`fee_no`"),Map.entry("feeType","b.`fee_type`"),Map.entry("amount","b.`amount`"),Map.entry("feeDate","b.`fee_date`"),Map.entry("shopId","b.`shop_id`"),Map.entry("remark","b.`remark`"),Map.entry("createdAt","b.`created_at`"),Map.entry("updatedAt","b.`updated_at`")));
numeric.put("other_fee",Set.of("id","feeNo","amount","shopId"));
dates.put("other_fee",Set.of("feeDate","createdAt","updatedAt"));
options.put("other_fee.feeType",Set.of("本土店租","跨境店","质量文件","耗材","其他"));
links.add(new Link("other_fee","shop_id","shop"));
columns.put("logistics_provider",Map.ofEntries(Map.entry("id","b.`id`"),Map.entry("code","b.`code`"),Map.entry("name","b.`name`"),Map.entry("warehouse","b.`warehouse`"),Map.entry("warehouseFee","b.`warehouse_fee`"),Map.entry("bank","b.`bank`"),Map.entry("system","b.`system`"),Map.entry("customerCode","b.`customer_code`"),Map.entry("createdAt","b.`created_at`"),Map.entry("updatedAt","b.`updated_at`")));
numeric.put("logistics_provider",Set.of("id","code"));
dates.put("logistics_provider",Set.of("createdAt","updatedAt"));
columns.put("studio_receipt",Map.ofEntries(Map.entry("id","b.`id`"),Map.entry("orderNo","b.`order_no`"),Map.entry("quantity","b.`quantity`"),Map.entry("boxCount","b.`box_count`"),Map.entry("boxSpec","b.`box_spec`"),Map.entry("boxWeight","b.`box_weight`"),Map.entry("receivedAt","b.`received_at`"),Map.entry("remark","b.`remark`"),Map.entry("createdAt","b.`created_at`"),Map.entry("updatedAt","b.`updated_at`"),Map.entry("calc0","v.`收货每箱数量`"),Map.entry("calc1","v.`箱体积/m3`"),Map.entry("calc2","v.`总箱重/kg`")));
numeric.put("studio_receipt",Set.of("id","quantity","boxCount","boxWeight","calc0","calc1","calc2"));
dates.put("studio_receipt",Set.of("receivedAt","createdAt","updatedAt"));
columns.put("payment_receipt",Map.ofEntries(Map.entry("id","b.`id`"),Map.entry("code","b.`code`"),Map.entry("paidAt","b.`paid_at`"),Map.entry("amount","b.`amount`"),Map.entry("exchangeRate","b.`exchange_rate`"),Map.entry("shopId","b.`shop_id`"),Map.entry("remark","b.`remark`"),Map.entry("createdAt","b.`created_at`"),Map.entry("updatedAt","b.`updated_at`")));
numeric.put("payment_receipt",Set.of("id","code","amount","exchangeRate","shopId"));
dates.put("payment_receipt",Set.of("paidAt","createdAt","updatedAt"));
links.add(new Link("payment_receipt","shop_id","shop"));
columns.put("shop",Map.ofEntries(Map.entry("id","b.`id`"),Map.entry("name","b.`name`"),Map.entry("nameRu","b.`name_ru`"),Map.entry("createdAt","b.`created_at`"),Map.entry("updatedAt","b.`updated_at`")));
numeric.put("shop",Set.of("id"));
dates.put("shop",Set.of("createdAt","updatedAt"));
columns.put("attachment",Map.ofEntries(Map.entry("id","b.`id`"),Map.entry("sourceTable","b.`source_table`"),Map.entry("fieldName","b.`field_name`"),Map.entry("fileName","b.`file_name`"),Map.entry("cosKey","b.`cos_key`"),Map.entry("cosUrl","b.`cos_url`"),Map.entry("sizeBytes","b.`size_bytes`"),Map.entry("mimeType","b.`mime_type`"),Map.entry("createdAt","b.`created_at`")));
numeric.put("attachment",Set.of("id","sizeBytes"));
dates.put("attachment",Set.of("createdAt"));
columns.put("returns",Map.ofEntries(Map.entry("id","b.`id`"),Map.entry("shopId","b.`shop_id`"),Map.entry("fulfillmentScheme","b.`fulfillment_scheme`"),Map.entry("shipmentNo","b.`shipment_no`"),Map.entry("articleNo","b.`article_no`"),Map.entry("sku","b.`sku`"),Map.entry("orderDate","b.`order_date`"),Map.entry("returnDate","b.`return_date`"),Map.entry("statusDate","b.`status_date`"),Map.entry("compensationDate","b.`compensation_date`"),Map.entry("pickupPointDate","b.`pickup_point_date`"),Map.entry("returnToSellerDate","b.`return_to_seller_date`"),Map.entry("freeStorageUntil","b.`free_storage_until`"),Map.entry("returnStatus","b.`return_status`"),Map.entry("compensationStatus","b.`compensation_status`"),Map.entry("mandatoryFlag","b.`mandatory_flag`"),Map.entry("returnReason","b.`return_reason`"),Map.entry("buyerComment","b.`buyer_comment`"),Map.entry("buyerType","b.`buyer_type`"),Map.entry("returnQty","b.`return_qty`"),Map.entry("packageOpened","b.`package_opened`"),Map.entry("destination","b.`destination`"),Map.entry("storageAddress","b.`storage_address`"),Map.entry("location","b.`location`"),Map.entry("storageDays","b.`storage_days`"),Map.entry("returnBarcode","b.`return_barcode`"),Map.entry("storageFeeRub","b.`storage_fee_rub`"),Map.entry("disposalFeeRub","b.`disposal_fee_rub`"),Map.entry("maxPriceRub","b.`max_price_rub`"),Map.entry("ozonProductName","b.`ozon_product_name`"),Map.entry("sourceFile","b.`source_file`"),Map.entry("importedAt","b.`imported_at`"),Map.entry("productId","p_view.id"),Map.entry("productNo","p_view.product_no"),Map.entry("productName","p_view.name")));
numeric.put("returns",Set.of("id","shopId","returnQty","storageDays","storageFeeRub","disposalFeeRub","maxPriceRub","productId","productNo"));
dates.put("returns",Set.of("orderDate","returnDate","statusDate","compensationDate","pickupPointDate","returnToSellerDate","freeStorageUntil","importedAt"));
links.add(new Link("returns","shop_id","shop"));
var shipmentColumns=new HashMap<>(columns.get("shipment"));
shipmentColumns.put("productNo","p_view.product_no");
shipmentColumns.put("productName","p_view.name");
shipmentColumns.put("orderPaidAt","r_purchase_id.paid_at");
shipmentColumns.put("backendPrice","p_view.backend_price");
shipmentColumns.put("orderPackedQty","po_view.`装箱总数`");
columns.put("shipment",Map.copyOf(shipmentColumns));
var studio_receiptColumns=new HashMap<>(columns.get("studio_receipt"));
studio_receiptColumns.put("productName","p_view.name");
columns.put("studio_receipt",Map.copyOf(studio_receiptColumns));
var shipmentNumbers=new HashSet<>(numeric.get("shipment"));shipmentNumbers.addAll(Set.of("productNo","backendPrice","orderPackedQty"));numeric.put("shipment",Set.copyOf(shipmentNumbers));
var shipmentDates=new HashSet<>(dates.get("shipment"));shipmentDates.add("orderPaidAt");dates.put("shipment",Set.copyOf(shipmentDates));
auto.putAll(Map.of("product","product_no","replenishment","replenish_no","shipment","shipment_no","logistics_fee","fee_no","other_fee","fee_no","logistics_provider","code","payment_receipt","code"));
 }
 private static void table(String t) { if(!columns.containsKey(t)) throw new ServiceException("不支持的业务表"); }
 public void requireTable(String t) { table(t); }
 private static String camel(String s) { var b=new StringBuilder();boolean upper=false;for(char c:s.toCharArray()){if(c=='_')upper=true;else{b.append(upper?Character.toUpperCase(c):c);upper=false;}}return b.toString(); }
 public <T> QueryWrapper<T> query(String t,OzonBusinessQuery q,PageQuery page) {
  table(t);var w=new QueryWrapper<T>();var cols=columns.get(t);
  if(q.getScopeShopId()!=null){
   Long shopId=q.getScopeShopId();
   if(shopId<=0||mapper.row("shop",shopId)==null)throw new ServiceException("所选店铺不存在，请回首页重新选择");
   if(!t.equals("logistics_provider"))w.apply(shopPredicate(t,"b"),shopId);
  }
  if(q.getKeyword()!=null&&!q.getKeyword().isBlank()) {
   var searchable=cols.keySet().stream().filter(k->!numeric.get(t).contains(k)&&!dates.get(t).contains(k)).toList();
   w.and(n->{
    for(String key:searchable)n.or().like(cols.get(key),q.getKeyword());
    if(q.getKeyword().matches("[0-9]{1,18}")){
     n.or().eq("b.id",q.getKeyword());
     if(auto.containsKey(t))n.or().eq("b."+auto.get(t),q.getKeyword());
    }
   });
  }
  if(q.getFilters()!=null) for(var e:q.getFilters().entrySet()){
   String c=cols.get(e.getKey()),value=e.getValue();
   if(c==null) throw new ServiceException("未知筛选字段");
   if(value==null||value.isBlank())continue;
   if(value.length()>1000)throw new ServiceException("筛选内容过长");
   if(dates.get(t).contains(e.getKey())) w.ge(c,value);
   else if(numeric.get(t).contains(e.getKey())){try{w.eq(c,new BigDecimal(value));}catch(NumberFormatException ex){throw new ServiceException("数字筛选格式错误");}}
   else w.like(c,value);
  }
  if(q.getEnds()!=null)for(var e:q.getEnds().entrySet()){
   if(!dates.get(t).contains(e.getKey()))throw new ServiceException("未知日期字段");
   if(e.getValue()!=null&&!e.getValue().isBlank())w.le(cols.get(e.getKey()),e.getValue());
  }
  if(q.getEquals()!=null)for(var e:q.getEquals().entrySet()){
   String column=cols.get(e.getKey()),value=e.getValue();
   if(column==null)throw new ServiceException("未知精确筛选字段");
   if(value==null||value.isBlank())continue;
   if(value.length()>1000)throw new ServiceException("筛选内容过长");
   if(numeric.get(t).contains(e.getKey())){try{w.eq(column,new BigDecimal(value));}catch(NumberFormatException ex){throw new ServiceException("数字筛选格式错误");}}
   else w.eq(column,value);
  }
  if(q.getConditions()!=null&&!q.getConditions().isBlank())applyConditions(w,t,cols,q.getConditions(),q.getConjunction());
  if(Boolean.TRUE.equals(q.getManualOrder())){
   if(q.getGroupFields()!=null&&!q.getGroupFields().isBlank()||q.getSortFields()!=null&&!q.getSortFields().isBlank())throw new ServiceException("手动顺序不能与分组或字段排序同时使用");
   w.orderByAsc("COALESCE((SELECT position_key FROM ozon_business_grid_metadata pos WHERE pos.table_name='"+t+"' AND pos.item_kind='row' AND pos.item_key=CAST(b.id AS CHAR)),-CAST(b.id AS DECIMAL(40,20)))");
   w.orderByDesc("b.id");return w;
  }
  var ordered=new LinkedHashSet<String>();
  appendViewOrder(w,cols,q.getGroupFields(),3,ordered);
  appendViewOrder(w,cols,q.getSortFields(),5,ordered);
  if(!ordered.isEmpty()){if(!ordered.contains("id"))w.orderByDesc("b.id");return w;}
  String sort=page.getOrderByColumn();if(sort==null||sort.isBlank())sort="id";
  String order=cols.get(sort);if(order==null)throw new ServiceException("不支持的排序字段");
  boolean asc="asc".equalsIgnoreCase(page.getIsAsc())||"ascending".equalsIgnoreCase(page.getIsAsc());
  w.orderBy(true,asc,order);if(!sort.equals("id"))w.orderByDesc("b.id");return w;
 }
 /** 多维表格风格的筛选：支持 是/不是/包含/不包含/为空/不为空/大于(等于)/小于(等于)，多条按 且/或 组合。 */
 private static final Set<String> conditionOps=Set.of("is","isNot","contains","notContains","isEmpty","isNotEmpty","gt","gte","lt","lte");
 private <T> void applyConditions(QueryWrapper<T> w,String t,Map<String,String> cols,String json,String conjunction){
  List<Map<String,String>> raw;
  try{raw=JsonUtils.parseObject(json,new TypeReference<List<Map<String,String>>>(){});}catch(Exception ex){throw new ServiceException("筛选条件格式错误");}
  if(raw==null||raw.isEmpty())return;
  if(raw.size()>40)throw new ServiceException("筛选条件过多");
  var list=new ArrayList<Map<String,String>>();
  for(var item:raw){
   if(item==null)continue;
   String field=item.get("field"),op=item.get("operator"),value=item.get("value");
   if(field==null||field.isBlank())throw new ServiceException("筛选条件缺少字段");
   if(cols.get(field)==null)throw new ServiceException("未知筛选字段");
   if(op==null||op.isBlank())op="is";
   if(!conditionOps.contains(op))throw new ServiceException("不支持的筛选条件");
   if(op.equals("isEmpty")||op.equals("isNotEmpty"))value="";
   else{
    if(value==null||value.isBlank())continue;
    if(value.length()>1000)throw new ServiceException("筛选内容过长");
   }
   list.add(Map.of("field",field,"operator",op,"value",value==null?"":value));
  }
  if(list.isEmpty())return;
  boolean or="or".equalsIgnoreCase(conjunction==null?"":conjunction.trim());
  w.and(outer->{
   for(int i=0;i<list.size();i++){
    if(or&&i>0)outer.or();
    var item=list.get(i);
    applyCondition(outer,t,cols,item.get("field"),item.get("operator"),item.get("value"));
   }
  });
 }
 private <T> void applyCondition(QueryWrapper<T> w,String t,Map<String,String> cols,String field,String op,String value){
  String col=cols.get(field);
  boolean numericField=numeric.get(t)!=null&&numeric.get(t).contains(field);
  boolean dateField=dates.get(t)!=null&&dates.get(t).contains(field);
  switch(op){
   case "isEmpty" -> { if(numericField||dateField)w.isNull(col); else w.and(x->x.isNull(col).or().eq(col,"")); }
   case "isNotEmpty" -> { if(numericField||dateField)w.isNotNull(col); else w.and(x->x.isNotNull(col).ne(col,"")); }
   case "is" -> w.eq(col,conditionValue(value,numericField&&!dateField));
   case "isNot" -> w.ne(col,conditionValue(value,numericField&&!dateField));
   case "contains" -> w.like(col,value);
   case "notContains" -> w.notLike(col,value);
   case "gt" -> w.gt(col,conditionValue(value,numericField&&!dateField));
   case "gte" -> w.ge(col,conditionValue(value,numericField&&!dateField));
   case "lt" -> w.lt(col,conditionValue(value,numericField&&!dateField));
   case "lte" -> w.le(col,conditionValue(value,numericField&&!dateField));
   default -> throw new ServiceException("不支持的筛选条件");
  }
 }
 private Object conditionValue(String value,boolean numericField){
  if(!numericField)return value;
  try{return new BigDecimal(value);}catch(NumberFormatException ex){throw new ServiceException("数字筛选格式错误");}
 }
 /** 按直接归属或已有业务关联限定店铺；物流商为共用资料。 */
 private String shopPredicate(String t,String alias){
  return switch(t){
   case "product","purchase_order","shipment","logistics_fee","other_fee","payment_receipt","returns" -> alias+".shop_id={0}";
   case "shop" -> alias+".id={0}";
   case "replenishment" -> "EXISTS(SELECT 1 FROM product scope_product WHERE scope_product.id="+alias+".product_id AND scope_product.shop_id={0})";
   case "studio_receipt" -> "EXISTS(SELECT 1 FROM purchase_order scope_order WHERE scope_order.order_no="+alias+".order_no AND scope_order.shop_id={0})";
   case "logistics_provider" -> "1=1";
   case "attachment" -> {
    var branches=new ArrayList<String>();
    for(String source:List.of("product","replenishment","purchase_order","shipment","logistics_fee","other_fee","logistics_provider","studio_receipt","payment_receipt","shop")){
     branches.add("("+alias+".source_table='"+source+"' AND EXISTS(SELECT 1 FROM "+source+" scope_source WHERE scope_source.feishu_record_id="+alias+".feishu_record_id AND "+shopPredicate(source,"scope_source")+"))");
    }
    yield "("+String.join(" OR ",branches)+")";
   }
   default -> throw new ServiceException("不支持的店铺筛选");
  };
 }
 /** 所有分组、排序字段均经白名单映射，不接受客户端SQL。 */
 private <T> void appendViewOrder(QueryWrapper<T> w,Map<String,String> cols,String value,int maximum,Set<String> seen){
  if(value==null||value.isBlank())return;
  String[] items=value.split(",",-1);
  if(items.length>maximum)throw new ServiceException("分组或排序项过多");
  for(String item:items){
   String[] pair=item.split(":",-1);
   if(pair.length!=2||!cols.containsKey(pair[0])||(!pair[1].equals("asc")&&!pair[1].equals("desc")))throw new ServiceException("分组或排序字段不正确");
   if(seen.add(pair[0]))w.orderBy(true,pair[1].equals("asc"),cols.get(pair[0]));
  }
 }
 /** 固定主键、审计、店铺范围和自动编号是运行所需的结构字段。 */
 private static final Set<String> protectedFields=Set.of("id","shopId","productId","purchaseId","createdAt","updatedAt","productNo","replenishNo","shipmentNo","feeNo","code","orderNo","sourceTable","feishuRecordId");
 public String physicalColumn(String table,String prop){
  table(table);
  if(protectedFields.contains(prop))throw new ServiceException("这是系统关联或编号字段，不能直接删除");
  String expression=columns.get(table).get(prop);
  if(expression==null)throw new ServiceException("字段不存在");
  if(expression.matches("b\\.`[a-z0-9_]+`"))return expression.substring(3,expression.length()-1);
  return null;
 }
 public List<String> removedFields(String table){table(table);return removedMapper.list(table);}
 /** 所有新增和修改都清空已删除字段，避免数据再次写入。 */
 public void clearRemovedAfterWrite(String table,Long id){
  for(String prop:removedFields(table)){
   String column=physicalColumn(table,prop);
   if(column!=null)removedMapper.clearOne(table,column,id);
  }
 }
 public void removeField(String table,String prop){
  String column=physicalColumn(table,prop);
  if(column!=null){
   if(!"YES".equals(removedMapper.nullable(table,column)))throw new ServiceException("数据库要求此字段非空，不能直接删除");
  }
  removedMapper.mark(table,prop);
  if(column!=null)removedMapper.clearAll(table,column);
 }
 public List<Long> relations(String t,Long id) {
  if(!t.equals("shipment")&&!t.equals("logistics_fee"))return List.of();
  return mapper.relations(t.equals("shipment")?"shipment_id":"logistics_fee_id",t.equals("shipment")?"logistics_fee_id":"shipment_id",id);
 }
 public String relationLabels(String t,Long id){return t.equals("shipment")?mapper.feeLabels(id):mapper.shipmentLabels(id);}
 public String revision(String t,Long id) {
  table(t);var row=mapper.row(t,id);if(row==null)throw new ServiceException("记录不存在或已删除");
  String raw=new TreeMap<>(row).toString()+relations(t,id).toString();
  try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8)));}
  catch(java.security.NoSuchAlgorithmException ex){throw new IllegalStateException(ex);}
 }
 public void lockForRead(String t,Long id){table(t);if(mapper.lockRow(t,id)==null)throw new ServiceException("记录不存在或已删除");}
 public void lockAndCheck(String t,Long id,String expected) {
  table(t);if(id==null||mapper.lockRow(t,id)==null)throw new ServiceException("记录不存在或已删除");
  if(!revision(t,id).equals(expected))throw new ServiceException("记录已被修改，请刷新后重新编辑");
 }
 /** 自动编号字段（数据库列名），新增行预览时使用；没有自动编号的表返回 null。 */
 public String numberColumn(String t){table(t);return auto.get(t);}
 public Long nextNumber(String t,String field) {
  table(t);if(!Objects.equals(auto.get(t),field))throw new ServiceException("不支持的编号");
  Long previous=mapper.lastNumber(t,field);
  long today=Long.parseLong(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE))*1000+1;
  return previous==null?today:Math.max(today,previous+1);
 }
 private Object value(BeanWrapperImpl bean,String field){return bean.isReadableProperty(camel(field))?bean.getPropertyValue(camel(field)):null;}
 private static Long id(Object x){return x==null?null:Long.valueOf(x.toString());}
 public void validate(String t,Object entity,Long current,List<Long> related) {
  table(t);var bean=new BeanWrapperImpl(entity);
  for(Link link:links)if(link.table.equals(t)){
   Object v=value(bean,link.column);
   if(v!=null&&mapper.lockRow(link.target,id(v))==null)throw new ServiceException("关联记录不存在，请重新选择");
  }
  for(var e:options.entrySet())if(e.getKey().startsWith(t+".")){
   Object v=value(bean,e.getKey().substring(t.length()+1));
   if(v!=null&&!v.toString().isEmpty()&&!e.getValue().contains(v.toString()))throw new ServiceException("请选择有效的选项");
  }
  Object spec=value(bean,"box_spec");
  if(spec!=null&&!spec.toString().isBlank()&&!spec.toString().matches("[0-9]+(\\.[0-9]+)?\\*[0-9]+(\\.[0-9]+)?\\*[0-9]+(\\.[0-9]+)?"))throw new ServiceException("箱规请填写长*宽*高，单位厘米，例如60*40*30");
  if(t.equals("product")){
   Object pack=value(bean,"unit_per_pack");
   if(pack==null||new BigDecimal(pack.toString()).signum()<=0)throw new ServiceException("每包数量必须大于0");
   Long parent=id(value(bean,"parent_id"));var seen=new HashSet<Long>();
   if(current!=null)seen.add(current);
   while(parent!=null){if(!seen.add(parent))throw new ServiceException("父产品不能形成循环关联");var p=mapper.lockRow("product",parent);parent=p==null?null:id(p.get("parent_id"));}
  }
  if(t.equals("purchase_order")&&current!=null){
   var before=mapper.row(t,current);
   if(!Objects.equals(before.get("order_no"),value(bean,"order_no"))&&mapper.countRef("studio_receipt","order_no",before.get("order_no"))>0)throw new ServiceException("该订单号已被工作室收货引用，请先调整收货记录");
  }
  if(t.equals("attachment")){
   String source=String.valueOf(value(bean,"source_table"));table(source);
   if(source.equals("attachment")||mapper.lockSource(source,String.valueOf(value(bean,"feishu_record_id")))==null)throw new ServiceException("附件所属记录不存在");
   Object url=value(bean,"cos_url");
   if(url!=null&&!url.toString().isBlank()&&!url.toString().matches("https?://[^\\s]+"))throw new ServiceException("附件地址必须是http或https链接");
  }
  if(related!=null){
   if(related.size()>500||related.contains(null))throw new ServiceException("关联记录数量不合法");
   String target=t.equals("shipment")?"logistics_fee":t.equals("logistics_fee")?"shipment":null;
   if(target==null)throw new ServiceException("不支持的关联");
   for(Long other:new TreeSet<>(related))if(mapper.lockRow(target,other)==null)throw new ServiceException("关联记录不存在，请重新选择");
  }
 }
 public void saveRelations(String t,Long id,List<Long> related){
  if(!t.equals("shipment")&&!t.equals("logistics_fee"))return;
  if(related==null)return;
  mapper.clearRelations(t.equals("shipment")?"shipment_id":"logistics_fee_id",id);
  for(Long other:new TreeSet<>(related))mapper.addRelation(t.equals("shipment")?id:other,t.equals("shipment")?other:id);
 }
 public void beforeDelete(String t,Long id){
  table(t);
  for(Link link:links)if(link.target.equals(t)&&mapper.countRef(link.table,link.column,id)>0)throw new ServiceException("该记录仍被其他业务引用，请先解除关联");
  if(!relations(t,id).isEmpty())throw new ServiceException("该记录仍有出货与物流费用关联，请先解除关联");
  var row=mapper.row(t,id);
  if(!t.equals("attachment")&&mapper.attachmentCount(t,String.valueOf(row.get("feishu_record_id")))>0)throw new ServiceException("该记录仍有附件，请先处理附件");
  if(t.equals("purchase_order")&&mapper.countRef("studio_receipt","order_no",row.get("order_no"))>0)throw new ServiceException("该订单仍被工作室收货引用");
 }
}
