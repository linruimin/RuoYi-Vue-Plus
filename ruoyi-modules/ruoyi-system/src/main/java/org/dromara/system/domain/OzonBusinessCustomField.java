package org.dromara.system.domain;

import lombok.Data;

/** User-created column shared by all views of one business table. */
@Data
public class OzonBusinessCustomField {
    private Long id;
    private String tableName;
    private String label;
    private String type;
}
