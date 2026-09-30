package org.dromara.system.mapper;
import org.apache.ibatis.annotations.*;
import java.util.*;
/** 字段标识符由 OzonBusinessSupport 的服务端白名单校验后才传入动态 SQL。 */
public interface OzonBusinessRemovedFieldMapper {
 @Select("SELECT item_key FROM ozon_business_grid_metadata WHERE table_name=#{table} AND item_kind='field'") List<String> list(@Param("table") String table);
 @Select("SELECT IS_NULLABLE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=#{table} AND COLUMN_NAME=#{column}") String nullable(@Param("table") String table,@Param("column") String column);
 @Insert("INSERT IGNORE INTO ozon_business_grid_metadata(table_name,item_kind,item_key) VALUES(#{table},'field',#{prop})") int mark(@Param("table") String table,@Param("prop") String prop);
 @Update("UPDATE ${table} SET `${column}`=NULL WHERE `${column}` IS NOT NULL") int clearAll(@Param("table") String table,@Param("column") String column);
 @Update("UPDATE ${table} SET `${column}`=NULL WHERE id=#{id} AND `${column}` IS NOT NULL") int clearOne(@Param("table") String table,@Param("column") String column,@Param("id") Long id);
}
