package org.dromara.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.system.domain.OzonBusinessCustomField;
import org.dromara.system.mapper.OzonBusinessCustomFieldMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** User-defined text and number columns for business grids. */
@DS("ozon")
@Service
@RequiredArgsConstructor
public class OzonBusinessCustomFieldService {
    private final OzonBusinessSupport support;
    private final OzonBusinessCustomFieldMapper mapper;

    public List<OzonBusinessCustomField> list(String table) {
        support.requireTable(table);
        return mapper.list(table);
    }

    public List<Map<String, Object>> values(String table, List<Long> ids) {
        support.requireTable(table);
        if (ids == null || ids.size() > 100 || ids.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new ServiceException("记录数量不正确");
        }
        return ids.isEmpty() ? List.of() : mapper.values(table, ids);
    }

    @Transactional(rollbackFor = Exception.class)
    public OzonBusinessCustomField add(String table, String label, String type) {
        support.requireTable(table);
        String name = label == null ? "" : label.trim();
        if (name.isEmpty() || name.length() > 80) throw new ServiceException("字段名称长度须为1到80字");
        if (!"text".equals(type) && !"number".equals(type)) throw new ServiceException("不支持的字段类型");
        OzonBusinessCustomField field = new OzonBusinessCustomField();
        field.setTableName(table);
        field.setLabel(name);
        field.setType(type);
        try {
            mapper.add(field);
        } catch (DuplicateKeyException ex) {
            throw new ServiceException("字段名称已存在");
        }
        return field;
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(String table, Long fieldId, Long rowId, String value) {
        support.requireTable(table);
        OzonBusinessCustomField field = mapper.field(table, fieldId);
        if (field == null) throw new ServiceException("字段不存在或已删除");
        if (rowId == null || rowId <= 0) throw new ServiceException("记录编号不正确");
        support.lockForRead(table, rowId);
        if (value == null || value.isEmpty()) {
            mapper.clear(fieldId, rowId);
            return;
        }
        if (value.length() > 1000) throw new ServiceException("字段内容不能超过1000字");
        if ("number".equals(field.getType())) {
            try {
                BigDecimal number = new BigDecimal(value);
                if (number.precision() > 24 || number.scale() > 8) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                throw new ServiceException("请输入有效数字，最多24位、8位小数");
            }
        }
        mapper.save(table, fieldId, rowId, value);
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(String table, Long fieldId) {
        support.requireTable(table);
        if (mapper.field(table, fieldId) == null) throw new ServiceException("字段不存在或已删除");
        mapper.remove(table, fieldId);
    }
}
