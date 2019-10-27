CREATE TABLE person (
    id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    ${commonColumns},
    CONSTRAINT person_pk PRIMARY KEY (id)
);


CREATE TABLE person_relation (
    self_id INT NOT NULL,
    other_id INT NOT NULL,
    relation_type CHAR(1),
    ${commonColumns},
    CONSTRAINT person_relation_pk PRIMARY KEY (self_id, other_id),
    CONSTRAINT person_fk FOREIGN KEY (self_id) REFERENCES person(id),
    CONSTRAINT person_other_fk FOREIGN KEY (other_id) REFERENCES person(id)
);


CREATE TABLE person_address (
    person_id INT NOT NULL,
    serial_num INT NOT NULL,
    postal_cd CHAR(7),
    city VARCHAR(100),
    ${commonColumns},
    CONSTRAINT person_address_pk PRIMARY KEY (person_id, serial_num),
    CONSTRAINT person_address_fk FOREIGN KEY (person_id) REFERENCES person(id)
);