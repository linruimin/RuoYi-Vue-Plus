CREATE TABLE IF NOT EXISTS ozon_business_custom_field (
    id BIGINT NOT NULL AUTO_INCREMENT,
    table_name VARCHAR(40) NOT NULL,
    item_kind VARCHAR(8) NOT NULL,
    field_id BIGINT DEFAULT NULL,
    row_id BIGINT DEFAULT NULL,
    label VARCHAR(80) DEFAULT NULL,
    field_type VARCHAR(12) DEFAULT NULL,
    value_text VARCHAR(1000) DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_business_custom_field_name (table_name, item_kind, label),
    UNIQUE KEY uq_business_custom_cell (table_name, field_id, row_id),
    KEY idx_business_custom_table (table_name, item_kind, field_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
