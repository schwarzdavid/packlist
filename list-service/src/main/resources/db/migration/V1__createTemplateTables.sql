CREATE TABLE templates
(
    template_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    icon       VARCHAR(255),
    created_by BIGINT NOT NULL
);

CREATE TABLE template_items
(
    template_item_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    template_id      BIGINT       NOT NULL,
    name             VARCHAR(255) NOT NULL,
    CONSTRAINT fk_templates_template_items FOREIGN KEY (template_id) REFERENCES templates (template_id)
        ON UPDATE cascade
        ON DELETE cascade
);