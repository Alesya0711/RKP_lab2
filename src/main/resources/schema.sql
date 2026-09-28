CREATE TABLE IF NOT EXISTS persons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    gender VARCHAR(20),
    nationality VARCHAR(50),
    height DOUBLE,
    weight DOUBLE,
    birth_date DATE,
    phone_number VARCHAR(20)
);