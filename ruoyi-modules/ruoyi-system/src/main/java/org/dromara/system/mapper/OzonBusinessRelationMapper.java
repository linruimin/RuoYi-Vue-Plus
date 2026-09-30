package org.dromara.system.mapper;
import org.apache.ibatis.annotations.*;
import java.util.*;
/** 内部关联校验，动态标识符仅接受服务端注册表常量。 */
public interface OzonBusinessRelationMapper {
@Select("SELECT * FROM ${table} WHERE id=#{id}") Map<String,Object> row(@Param("table") String table,@Param("id") Long id);
@Select("SELECT * FROM ${table} WHERE id=#{id} FOR UPDATE") Map<String,Object> lockRow(@Param("table") String table,@Param("id") Long id);
@Select("SELECT COUNT(*) FROM ${table} WHERE ${column}=#{value}") Long countRef(@Param("table") String table,@Param("column") String column,@Param("value") Object value);
@Select("SELECT ${other} FROM shipment_logistics_fee WHERE ${own}=#{id} ORDER BY ${other}") List<Long> relations(@Param("own") String own,@Param("other") String other,@Param("id") Long id);
@Delete("DELETE FROM shipment_logistics_fee WHERE ${own}=#{id}") int clearRelations(@Param("own") String own,@Param("id") Long id);
@Insert("INSERT INTO shipment_logistics_fee(shipment_id,logistics_fee_id) VALUES(#{shipmentId},#{feeId})") int addRelation(@Param("shipmentId") Long shipmentId,@Param("feeId") Long feeId);
@Select("SELECT ${column} FROM ${table} ORDER BY ${column} DESC LIMIT 1 FOR UPDATE") Long lastNumber(@Param("table") String table,@Param("column") String column);
@Select("SELECT COUNT(*) FROM attachment WHERE source_table=#{table} AND feishu_record_id=#{source}") Long attachmentCount(@Param("table") String table,@Param("source") String source);
@Select("SELECT COUNT(*) FROM ${table} WHERE feishu_record_id=#{source}") Long sourceCount(@Param("table") String table,@Param("source") String source);
@Select("SELECT GROUP_CONCAT(CONCAT(COALESCE(f.fee_no,f.id),' · ',COALESCE(f.fee_type,'')) ORDER BY f.id SEPARATOR '、') FROM shipment_logistics_fee x JOIN logistics_fee f ON f.id=x.logistics_fee_id WHERE x.shipment_id=#{id}")
String feeLabels(Long id);
@Select("SELECT GROUP_CONCAT(CONCAT(COALESCE(s.shipment_no,s.id),' · ',p.order_no) ORDER BY s.id SEPARATOR '、') FROM shipment_logistics_fee x JOIN shipment s ON s.id=x.shipment_id JOIN purchase_order p ON p.id=s.purchase_id WHERE x.logistics_fee_id=#{id}")
String shipmentLabels(Long id);
@Select("SELECT id FROM ${table} WHERE feishu_record_id=#{source} FOR UPDATE")
Long lockSource(@Param("table") String table,@Param("source") String source);
}
