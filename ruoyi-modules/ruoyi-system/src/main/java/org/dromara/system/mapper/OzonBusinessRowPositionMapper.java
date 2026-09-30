package org.dromara.system.mapper;
import org.apache.ibatis.annotations.*;
import java.math.BigDecimal;
import java.util.*;
/** 动态表名只接受服务端已注册的业务表常量。 */
public interface OzonBusinessRowPositionMapper {
 @Select("SELECT b.id,COALESCE((SELECT p.position_key FROM ozon_business_grid_metadata p WHERE p.table_name=#{table} AND p.item_kind='row' AND p.item_key=CAST(b.id AS CHAR)),-CAST(b.id AS DECIMAL(40,20))) AS position_key FROM ${table} b ORDER BY position_key,b.id DESC")
 List<Map<String,Object>> orderedRows(@Param("table") String table);
 @Insert("INSERT INTO ozon_business_grid_metadata(table_name,item_kind,item_key,position_key) VALUES(#{table},'row',#{id},#{position}) ON DUPLICATE KEY UPDATE position_key=VALUES(position_key)")
 int save(@Param("table") String table,@Param("id") Long id,@Param("position") BigDecimal position);
 @Insert("INSERT INTO ozon_business_grid_metadata(table_name,item_kind,item_key,position_key) VALUES(#{table},'lock','__lock__',0) ON DUPLICATE KEY UPDATE position_key=position_key") void lockTable(@Param("table") String table);
}
