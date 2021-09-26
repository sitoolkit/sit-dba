CREATE TABLE main (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100),
    col_char CHAR(1),
    col_date DATE,
    col_time TIME,
    col_timestamp TIMESTAMP,
    col_jsonb JSONB,
    ${commonColumns}
);


-- CREATE TABLE one_to_one (
--     id CHAR(36) PRIMARY KEY REFERENCES main,
--     name VARCHAR(100),
--     ${commonColumns}
-- );


CREATE TABLE one_to_many (
    id CHAR(36) PRIMARY KEY,
    main_id CHAR(36) REFERENCES main,
    name VARCHAR(100),
    ${commonColumns}
);


CREATE TABLE many_to_many (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100),
    ${commonColumns}
);


CREATE TABLE main_many_to_many_rel (
    main_id CHAR(36) REFERENCES main,
    many_to_many_id CHAR(36) REFERENCES many_to_many,
    CONSTRAINT main_many_to_many_rel_pk PRIMARY KEY (main_id, many_to_many_id)
);


CREATE TABLE composite_key (
    id_1 CHAR(3),
    id_2 CHAR(3),
    name VARCHAR(100),
    ${commonColumns},
    CONSTRAINT composite_id_pk PRIMARY KEY (id_1, id_2)
);