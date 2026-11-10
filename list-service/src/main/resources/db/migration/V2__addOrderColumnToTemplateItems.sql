ALTER TABLE template_items
    ADD COLUMN position INTEGER;
UPDATE template_items ti
SET position = numbered.position
FROM (SELECT template_item_id, ROW_NUMBER() OVER (PARTITION BY template_id ORDER BY template_item_id) - 1 AS position
      FROM template_items) AS numbered
WHERE ti.template_item_id = numbered.template_item_id;