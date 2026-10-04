package org.dromara.system.mapper;
import org.apache.ibatis.annotations.*;
import java.math.BigDecimal;
import java.util.*;
/**
 * 动态表名只接受服务端已注册的业务表常量。
 *
 * ⚠️ `CAST(b.id AS CHAR)` 的 collation 取自连接的 `collation_connection`（MySQL 8 默认为
 * `utf8mb4_0900_ai_ci`），与列的 collation 同为 IMPLICIT 级别，两边不一致就会抛
 * `1267 Illegal mix of collations`（2026-10-04 在「2.出货 / 在下方插入行」上踩到）。
 * 这里显式 COLLATE，使比较不依赖服务器/表的 collation 设置。
 */
public interface OzonBusinessRowPositionMapper {
 @Select("SELECT b.id,COALESCE((SELECT p.position_key FROM ozon_business_grid_metadata p WHERE p.table_name=#{table} AND p.item_kind='row' AND p.item_key=CAST(b.id AS CHAR) COLLATE utf8mb4_0900_ai_ci),-CAST(b.id AS DECIMAL(40,20))) AS position_key FROM ${table} b ORDER BY position_key,b.id DESC")
 List<Map<String,Object>> orderedRows(@Param("table") String table);
 @Insert("INSERT INTO ozon_business_grid_metadata(table_name,item_kind,item_key,position_key) VALUES(#{table},'row',#{id},#{position}) ON DUPLICATE KEY UPDATE position_key=VALUES(position_key)")
 int save(@Param("table") String table,@Param("id") Long id,@Param("position") BigDecimal position);
 @Insert("INSERT INTO ozon_business_grid_metadata(table_name,item_kind,item_key,position_key) VALUES(#{table},'lock','__lock__',0) ON DUPLICATE KEY UPDATE position_key=position_key") void lockTable(@Param("table") String table);
}
