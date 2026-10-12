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
