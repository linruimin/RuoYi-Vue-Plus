package org.dromara.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.*;
import org.dromara.system.domain.OzonBusinessCustomField;

/** Values stay separate from fixed business and calculated columns. */
public interface OzonBusinessCustomFieldMapper {
    @Select("SELECT id, table_name AS tableName, label, field_type AS type FROM ozon_business_custom_field WHERE item_kind='field' AND table_name=#{table} ORDER BY id")
    List<OzonBusinessCustomField> list(@Param("table") String table);

    @Select("SELECT id, table_name AS tableName, label, field_type AS type FROM ozon_business_custom_field WHERE item_kind='field' AND table_name=#{table} AND id=#{id}")
    OzonBusinessCustomField field(@Param("table") String table, @Param("id") Long id);

    @Insert("INSERT INTO ozon_business_custom_field(table_name,item_kind,label,field_type) VALUES(#{tableName},'field',#{label},#{type})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int add(OzonBusinessCustomField field);

    @Select("<script>SELECT v.field_id AS fieldId, v.row_id AS rowId, v.value_text AS value FROM ozon_business_custom_field v JOIN ozon_business_custom_field f ON f.id=v.field_id AND f.item_kind='field' WHERE v.item_kind='value' AND f.table_name=#{table} AND v.row_id IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    List<Map<String, Object>> values(@Param("table") String table, @Param("ids") List<Long> ids);

    @Insert("INSERT INTO ozon_business_custom_field(table_name,item_kind,field_id,row_id,value_text) VALUES(#{table},'value',#{fieldId},#{rowId},#{value}) ON DUPLICATE KEY UPDATE value_text=#{value}")
    int save(@Param("table") String table, @Param("fieldId") Long fieldId, @Param("rowId") Long rowId, @Param("value") String value);

    @Delete("DELETE FROM ozon_business_custom_field WHERE item_kind='value' AND field_id=#{fieldId} AND row_id=#{rowId}")
    int clear(@Param("fieldId") Long fieldId, @Param("rowId") Long rowId);

    @Delete("DELETE FROM ozon_business_custom_field WHERE item_kind='field' AND table_name=#{table} AND id=#{id}")
    int remove(@Param("table") String table, @Param("id") Long id);
}
