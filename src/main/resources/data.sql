INSERT INTO cities (name, postal_code) VALUES ('Москва', '101000');
INSERT INTO cities (name, postal_code) VALUES ('Санкт-Петербург', '190000');
INSERT INTO cities (name, postal_code) VALUES ('Владимир', '600000');
INSERT INTO cities (name, postal_code) VALUES ('Казань', '420000');

INSERT INTO addresses (street, house, apartment, city_id) VALUES ('Тверская', '12', '45', 1);
INSERT INTO addresses (street, house, apartment, city_id) VALUES ('Невский проспект', '28', '10', 2);
INSERT INTO addresses (street, house, apartment, city_id) VALUES ('Железнодорожная', '42', '4', 3);
INSERT INTO addresses (street, house, apartment, city_id) VALUES ('Цветной бульвар', '65', '88', 4);

INSERT INTO persons (last_name, first_name, middle_name, gender, nationality, height, weight, birth_date, phone_number, address_id)
VALUES ('Донской', 'Дмитрий', 'Иванович', 'Мужской', 'Русский', 180.0, 75.5, '2000-05-12', '+79991112233', 1);

INSERT INTO persons (last_name, first_name, middle_name, gender, nationality, height, weight, birth_date, phone_number, address_id)
VALUES ('Комарова', 'Светлана', 'Сергеевна', 'Женский', 'Русская', 165.0, 55.0, '2005-10-20', '+79993332211', 2);

INSERT INTO persons (last_name, first_name, middle_name, gender, nationality, height, weight, birth_date, phone_number, address_id)
VALUES ('Кузнецов', 'Федор', 'Николаевич', 'Мужской', 'Русский', 192.0, 88.5, '2001-07-08', '+79998884455', 3);

INSERT INTO persons (last_name, first_name, middle_name, gender, nationality, height, weight, birth_date, phone_number, address_id)
VALUES ('Васькина', 'Мария', 'Викторовна', 'Женский', 'Русская', 169.0, 72.0, '2006-11-06', '+79997776622', 4);