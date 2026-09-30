package org.dromara.system.service.impl;
import com.baomidou.dynamic.datasource.annotation.DS;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/** 跨全部视图永久移除字段及其历史值。 */
@DS("ozon") @Service @RequiredArgsConstructor
public class OzonBusinessRemovedFieldService {
 private final OzonBusinessSupport support;
 public List<String> list(String table){return support.removedFields(table);}
 @Transactional(rollbackFor=Exception.class)
 public void remove(String table,String prop){support.removeField(table,prop);}
}
