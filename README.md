# Лабораторная работа №6: Аутентификация пользователей (Spring Security)

## Цель работы
Научиться использовать фреймворк Spring Security для аутентификации и авторизации пользователей: добавить в приложение страницу входа, хранить учётные данные в БД с хешированием паролей и разграничить доступ к страницам и REST-методам в зависимости от роли пользователя.

## Архитектура проекта
- **Стек:** Java 17, Spring Boot 4.0.8, Maven, Spring Web MVC, Spring Security, Spring JDBC, Thymeleaf, Hibernate Validator, Bootstrap 5.
- **СУБД:** H2 (in-memory). Структуру создаёт `schema.sql`, начальные данные (в том числе пользователей) загружает `data.sql`.
- **Основные слои:**
  - **Controller** — `PersonController` (веб-интерфейс на Thymeleaf), `PersonRestController` (REST API), `LoginController` (страница входа), `GlobalModelAttributes` (передача в шаблоны имени и роли текущего пользователя).
  - **Service** — `UserService` (реализация `UserDetailsService`, загружает пользователя для Spring Security).
  - **Repository** — `PersonRepository`, `CityRepository`, `UserRepository` (SQL-запросы через `JdbcTemplate`).
  - **Config** — `SecurityConfig` (цепочка фильтров безопасности, правила доступа, кодировщик паролей).
  - **Exception** — `NotFoundException`, `GlobalExceptionHandler` (ответ `404` для REST).
- **Ключевые аннотации:**
  - `@Configuration`, `@EnableWebSecurity` — класс настройки безопасности.
  - `@Bean` — бины `SecurityFilterChain` и `PasswordEncoder`.
  - `@Service` — `UserService`.
  - `@Repository` — репозитории на `JdbcTemplate`.
  - `@ControllerAdvice`, `@ModelAttribute` — общие атрибуты для всех шаблонов.
  - `@RestControllerAdvice`, `@ExceptionHandler` — обработка исключений в REST.
  - `@Valid` — проверка входных данных.
- **Описание моделей данных и DTO:**
  - `Role` — перечисление ролей: `ROLE_USER`, `ROLE_ADMIN`.
  - `AppUser` — пользователь системы: `id`, `username`, `password` (хеш BCrypt), `role`. Таблица `users`.
  - `Person`, `Address`, `City` — предметная область (человек, адрес, город), простые классы без аннотаций JPA.
  - `PersonRequestDTO` — данные запроса на создание/изменение человека (с валидацией).
  - `PersonResponseDTO` — данные ответа REST (ФИО и адрес в виде строк).

## Алгоритм работы

1. Пользователь обращается к любой странице, например `/persons`.
2. Цепочка фильтров Spring Security проверяет, аутентифицирован ли пользователь. Если нет, браузер перенаправляется на `/login`, а REST-клиент получает `401 Unauthorized`.
3. Пользователь вводит логин и пароль. Форма отправляется методом POST на `/login` вместе с CSRF-токеном (добавляется автоматически через `th:action`). Для REST используется HTTP Basic.
4. Spring вызывает `UserService.loadUserByUsername(...)`, который через `UserRepository` ищет пользователя в таблице `users`. Если пользователь не найден, выбрасывается `UsernameNotFoundException`, и вход отклоняется.
5. Spring сравнивает введённый пароль с хешем из БД с помощью `BCryptPasswordEncoder`. Пароли хранятся только в виде хеша BCrypt.
6. При успешной проверке пользователь сохраняется в сессии как аутентифицированный с ролью из БД, и пользователь перенаправляется на `/persons`.
7. При каждом запросе проверяется соответствие роли правилам доступа из `SecurityConfig`. Если роли недостаточно, возвращается `403 Forbidden`.
8. В шаблонах кнопки добавления и удаления отображаются только администратору (флаг `isAdmin` из `GlobalModelAttributes`). Реальная защита выполняется на сервере правилами `SecurityConfig`, а скрытие кнопок служит только удобству.
9. Выход выполняется POST-запросом на `/logout` с последующим перенаправлением на `/login?logout`.

### Тестовые пользователи

| Логин | Пароль | Роль |
|---|---|---|
| `admin` | `admin123` | `ROLE_ADMIN` |
| `user` | `user123` | `ROLE_USER` |

### Разграничение доступа

| Ресурс | Аноним | USER | ADMIN |
|---|---|---|---|
| `/login` | + | + | + |
| `/persons` (список, поиск) | - | + | + |
| `/persons/edit/**`, `/persons/save` (редактирование) | - | + | + |
| `/persons/new` (добавление) | - | - | + |
| `/persons/delete/**` (удаление) | - | - | + |
| `GET /api/persons/**` | - | + | + |
| `PUT /api/persons/{id}`, `PATCH /api/persons/{id}/relocate` | - | + | + |
| `POST /api/persons`, `DELETE /api/persons/{id}` | - | - | + |

Сохранение новой записи через `/persons/save` пользователю с ролью `USER` дополнительно запрещено проверкой в `PersonController`.




# Лабораторная работа №5: Работа с реляционными БД через JDBCTemplate

## Цель работы

Создание запросов к реляционной базе данных с использованием чистого SQL через инструмент JdbcTemplate, отказ от ORM (Hibernate/JPA) и реализация ручного маппинга данных.

## 🔗 Вариант 1: «Человек»

Сущность содержит: фамилия; имя; отчество; пол; национальность; рост; вес; дата рождения; номер телефона; домашний адрес (связь с таблицами городов и адресов).

## Архитектура и стек технологий

- **Платформа:** Java 17 / 21, Spring Boot 3.2.x
- **База данных:** H2 Database (In-Memory) + Spring JDBC (JdbcTemplate)
- **API:** Spring Web (REST Controller)
- **Паттерны:** DTO (Data Transfer Object), DAO (Data Access Object), Data Mapper (RowMapper).
- **Валидация:** Hibernate Validator (JSR 380)
- **Формат данных:** JSON (маршаллинг через Jackson).

## Реализованные методы (Endpoints)

### Базовые CRUD-операции

- **GET /api/persons** — Получить список всех людей (Статус 200 OK). Работает через RowMapper и SQL-запрос с LEFT JOIN.
- **GET /api/persons/{id}** — Получить человека по ID (Статус 200 OK или 404 Not Found).
- **POST /api/persons** — Создать новую запись (Статус 201 Created). Сохранение связанных таблиц реализовано через KeyHolder и @Transactional.
- **PUT /api/persons/{id}** — Полностью обновить данные человека по ID (Статус 200 OK). Выполняется каскадный UPDATE.
- **DELETE /api/persons/{id}** — Удалить человека по ID (Статус 204 No Content).

### Расширенная бизнес-логика

- **GET /api/persons/statistics** — Аналитика. Возвращает общее количество людей, средний рост, а также распределение по полу.
- **PATCH /api/persons/{id}/relocate** — Сложное обновление с частичными данными. Оформление переезда: принимает newCityId и newStreet в качестве параметров запроса и точечно обновляет только адрес человека, не затрагивая остальные данные.

### Методы фильтрации (Кастомные SQL-запросы)

- Поиск по фамилии (`LIKE LOWER(?)`).
- Поиск по названию города (через связанную таблицу).
- Поиск по полу (с игнорированием регистра).



# Лабораторная работа №4: Построение REST API

## Цель работы
Создание Spring-приложения с несколькими таблицами с использованием REST API.

## Вариант 1: «Человек»
Сущность содержит: фамилия; имя; отчество; пол; национальность; рост; вес; дата рождения; номер телефона; домашний адрес (связь с таблицами городов и адресов).

## Архитектура и стек технологий
* **Платформа:** Java 17 / 21, Spring Boot 3.2.x
* **База данных:** H2 Database (In-Memory) + Spring Data JPA
* **API:** Spring Web (REST Controller)
* **Паттерны:** DTO (Data Transfer Object) для избежания циклических ссылок и изоляции БД.
* **Валидация:** Hibernate Validator (JSR 380)
* **Формат данных:** JSON (маршаллинг через Jackson).

## Реализованные методы (Endpoints)

### Базовые CRUD-операции
* `GET /api/persons` — Получить список всех людей (Статус `200 OK`).
* `GET /api/persons/{id}` — Получить человека по ID (Статус `200 OK` или `404 Not Found`).
* `POST /api/persons` — Создать новую запись (Статус `201 Created`). Принимает `PersonRequestDTO` с валидацией.
* `PUT /api/persons/{id}` — Полностью обновить данные человека по ID (Статус `200 OK`).
* `DELETE /api/persons/{id}` — Удалить человека по ID (Статус `204 No Content`).

### Расширенная бизнес-логика
* `GET /api/persons/statistics` — **Аналитика**. Возвращает общее количество людей, средний рост, а также распределение по полу.
* `PATCH /api/persons/{id}/relocate` — **Сложное обновление с частичными данными**. Оформление переезда: принимает `newCityId` и `newStreet` в качестве параметров запроса и точечно обновляет только адрес человека, не затрагивая остальные данные.

## Демонстрация работы 

1. Создание записи (POST)
   <img width="647" height="857" alt="image" src="https://github.com/user-attachments/assets/d4d7feb4-b8f7-4f2d-b5ac-937ccfdd987c" />
   ![Validation Error](link_to_image_2)
2. Получение списка (GET)
   <img width="893" height="687" alt="image" src="https://github.com/user-attachments/assets/65d7228b-1819-41f4-8ce0-638930fee8da" />
3. Переезд (PATCH)
   <img width="848" height="665" alt="image" src="https://github.com/user-attachments/assets/19ecda22-ffe5-461d-9742-6c0b60eadb87" />
4. Статистика (GET)
  <img width="560" height="393" alt="image" src="https://github.com/user-attachments/assets/ae5f7b0e-b3eb-42e5-a249-863c87ecbd55" />
5. Удаление (DELETE 204 No Content)
   <img width="645" height="257" alt="image" src="https://github.com/user-attachments/assets/7ec6be91-9711-4365-a349-a7dac18d1928" />



# Лабораторная работа №3

## Цель работы

Создание Spring-приложения с несколькими взаимосвязанными таблицами с использованием Java Persistence API (JPA).

## Архитектура проекта

**Стек:** Java 17 / 21, Spring Boot 3.2.5, Spring Web MVC, Spring Data JPA, Hibernate Validator, Thymeleaf, H2 Database, Maven, HTML5, Bootstrap 5.

## Структура базы данных

1. **`cities`** (Города) — справочник городов с почтовыми индексами.
2. **`addresses`** (Адреса) — содержит улицу, дом, квартиру и внешний ключ `city_id` (связь *много к одному* с таблицей городов).
3. **`persons`** (Люди) — содержит данные (фамилия, имя, отчество, пол, национальность, рост, вес, дата рождения, телефон) и внешний ключ `address_id` (связь *много к одному* с таблицей адресов).

## Основные компоненты

* **Entities (`City.java`, `Address.java`, `Person.java`)**
  JPA-сущности, описывающие структуру таблиц в базе данных, содержащие аннотации сопоставления (`@Entity`, `@Table`, `@ManyToOne`, `@JoinColumn`), настройки каскадирования и ограничения полей (`@Column`).

* **Repositories (`CityRepository.java`, `PersonRepository.java`)**
  Интерфейсы, наследующие `JpaRepository`. `PersonRepository` дополнительно содержит методы для расширенного поиска:
  * Поиск по фамилии (`findByLastNameContainingIgnoreCase`).
  * Поиск по названию города через связанные таблицы (`findByAddressCityNameContainingIgnoreCase`).
  * Поиск по полу (`findByGenderIgnoreCase`).

* **Controller (`PersonController.java`)**
  Веб-контроллер, обрабатывающий GET- и POST-запросы. Обеспечивает отображение списка, вывод форм создания/редактирования, валидацию входящих данных (`@Valid`), связывание сущностей с выбранным городом и фильтрацию данных по критериям.

* **View (`persons.html`, `person-form.html`)**
  HTML-шаблоны Thymeleaf с использованием Bootstrap 5. Обеспечивают отображение таблиц со вложенными свойствами, форму с динамическим выпадающим списком городов (`<select>`) и панель критериального поиска.

* **База данных и инициализация (H2 Database, `schema.sql`, `data.sql`)**
  Встроенная реляционная БД работает в оперативной памяти. Файл `schema.sql` создает схему из трех таблиц с внешними ключами при старте, а `data.sql` автоматически заполняет их начальными тестовыми записями.

## Поиск по критериям
* **Поиск по фамилии** 
<img width="1326" height="232" alt="image" src="https://github.com/user-attachments/assets/18913abc-3211-428b-a964-a614271516e2" />

* **Поиск по городу** 
<img width="1335" height="245" alt="image" src="https://github.com/user-attachments/assets/f7338d5b-9137-471b-abe9-791470918dfc" />

* **Поиск по полу** 
<img width="1312" height="272" alt="image" src="https://github.com/user-attachments/assets/1867be82-1404-4a97-b618-9e6232467806" />


# Лабораторная работа №2

## Цель работы

Изучить работу Spring Data JPA на примере создания веб-приложения для управления данными о людях.

## Архитектура проекта

**Стек:** Java 17 / 21, Spring Boot 3.2.5, Spring Web MVC, Spring Data JPA, Thymeleaf, H2 Database, Maven, HTML5, CSS3 / Bootstrap 5.

## Основные компоненты

* **Entity — `Person.java`**
  JPA-сущность описывает человека и хранит его фамилию, имя, отчество, пол, национальность, рост, вес, дату рождения и номер телефона.

* **Repository — `PersonRepository.java`**
  Репозиторий наследуется от `JpaRepository` (расширяющий `CrudRepository`) и предоставляет готовые операции для получения, добавления, изменения и удаления записей о людях в базе данных.

* **Controller — `PersonController.java`**
  Контроллер обрабатывает GET- и POST-запросы, взаимодействует с репозиторием и обеспечивает просмотр общего списка, вывод подробных деталей по ID, добавление, редактирование и удаление записей с предварительной проверкой их существования в БД.

* **View — `persons.html`, `person-details.html` и `person-form.html`**
  HTML-шаблоны Thymeleaf отвечают за отображение списка людей, просмотр подробной информации и универсальную форму для создания и редактирования данных.

* **База данных — H2 Database, `schema.sql` и `data.sql`**
  Встроенная база данных H2 хранит сведения в оперативной памяти (in-memory). Файл `schema.sql` формирует структуру таблицы, а `data.sql` автоматически заполняет её начальными тестовыми данными при запуске приложения.

* **Оформление — Bootstrap 5**
  Фреймворк стилей отвечает за адаптивную верстку, оформление таблиц, карточек детальной информации, кнопок управления и интерактивных полей ввода.

  ## Скриншоты экрана

* **Список людей**
  <img width="1662" height="371" alt="image" src="https://github.com/user-attachments/assets/0ecc0ece-a315-4612-af4a-58b35bb11c6e" />

 * **Детальная информация**
 <img width="737" height="701" alt="image" src="https://github.com/user-attachments/assets/c6740687-6173-4d70-8376-e347e2b172a7" />

 * **Редактирование информации**
<img width="870" height="607" alt="image" src="https://github.com/user-attachments/assets/452664e0-f197-4684-9b40-6a9c7750becf" />

 * **Добавление нового человека**
<img width="868" height="610" alt="image" src="https://github.com/user-attachments/assets/0f7d3fc6-3095-4615-acd9-765dfb683036" />
