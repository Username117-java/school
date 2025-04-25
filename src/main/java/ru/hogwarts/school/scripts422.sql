DROP TABLE IF EXISTS drivers;
DROP TABLE IF EXISTS cars;

CREATE TABLE cars
(
    id    SERIAL PRIMARY KEY,
    brand text,
    model text,
    price INTEGER
);

CREATE TABLE drivers
(
    id             SERIAL PRIMARY KEY,
    name           TEXT,
    age            INTEGER,
    driver_license BOOLEAN,
    car_id         INTEGER REFERENCES cars (id)
);

