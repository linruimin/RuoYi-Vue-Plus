package org.dromara.system.service.impl;
import com.baomidou.dynamic.datasource.annotation.DS;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.system.mapper.OzonBusinessRowPositionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/** 在默认手动顺序中持久插入记录，不改变记录的业务字段。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBusinessRowPositionService {
 private static final Set<String> TABLES=Set.of("product","replenishment","purchase_order","shipment","logistics_fee","other_fee","logistics_provider","studio_receipt","payment_receipt","shop","attachment");
 private final OzonBusinessRowPositionMapper mapper;
 @Transactional(rollbackFor=Exception.class)
 public void place(String table,Long rowId,Long anchorId,String placement){
  if(!TABLES.contains(table))throw new ServiceException("不支持的业务表");
  if(rowId==null||anchorId==null||rowId.equals(anchorId)||!("above".equals(placement)||"below".equals(placement)))throw new ServiceException("插入位置不正确");
  mapper.lockTable(table);
   var ordered=mapper.orderedRows(table);
   var anchorIndex=-1;var rowFound=false;
   for(int i=0;i<ordered.size();i++){
    long id=((Number)ordered.get(i).get("id")).longValue();
    if(id==anchorId)anchorIndex=i;
    if(id==rowId)rowFound=true;
   }
   if(!rowFound||anchorIndex<0)throw new ServiceException("记录不存在，请刷新后重试");
   var anchor=number(ordered.get(anchorIndex));
   BigDecimal before=null,after=null;
   if("above".equals(placement)){
    after=anchor;
    for(int i=anchorIndex-1;i>=0;i--)if(((Number)ordered.get(i).get("id")).longValue()!=rowId){before=number(ordered.get(i));break;}
   }else{
    before=anchor;
    for(int i=anchorIndex+1;i<ordered.size();i++)if(((Number)ordered.get(i).get("id")).longValue()!=rowId){after=number(ordered.get(i));break;}
   }
   BigDecimal next=before==null?after.subtract(BigDecimal.ONE):after==null?before.add(BigDecimal.ONE):before.add(after).divide(BigDecimal.valueOf(2),20,RoundingMode.HALF_UP);
   if((before!=null&&next.compareTo(before)<=0)||(after!=null&&next.compareTo(after)>=0))throw new ServiceException("此处插入次数过多，请联系管理员整理顺序");
   mapper.save(table,rowId,next);
 }
 private BigDecimal number(Map<String,Object> row){return new BigDecimal(row.get("position_key").toString());}
}
