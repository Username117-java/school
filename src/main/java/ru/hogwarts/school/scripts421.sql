ALTER TABLE Student ADD CONSTRAINT age_constraint CHECK ( age > 16 );

ALTER TABLE Student ADD CONSTRAINT name_unique UNIQUE (name);
ALTER TABLE Student ALTER COLUMN name SET NOT NULL;

ALTER TABLE Faculty ADD CONSTRAINT name_color_unique UNIQUE (name, color);

ALTER TABLE Student ALTER COLUMN age SET DEFAULT 20;



