# 🎵 Muzify — backend музыкального стримингового сервиса

Серверное приложение на **Java 17 + Spring Boot 3**, реализующее REST API музыкального
сервиса: каталог треков, избранное, история прослушиваний, статистика для исполнителей и
администрирование пользователей.

**Автор:** _ФИО студента_

## Какую проблему решает
Слушателю нужен единый каталог музыки, где можно быстро найти трек, сохранить любимые и
вернуться к истории прослушиваний. Исполнителю — публиковать свои треки и видеть, как их
слушают. Администратору — управлять пользователями и следить за системой.

## Роли
| Роль | Возможности |
|------|-------------|
| `USER` | регистрация/вход, поиск и просмотр треков, избранное, прослушивание, история |
| `ARTIST` | всё, что `USER` + добавление/изменение/удаление **своих** треков, статистика (`/api/artist/stats`) |
| `ADMIN` | всё, что `ARTIST` + управление любыми треками, список пользователей, смена ролей, общая статистика |

## Технологии
Java 17 · Spring Boot 3.2 · Spring Web · Spring Data JPA/Hibernate · Spring Security + JWT (jjwt) ·
Bean Validation · H2 / PostgreSQL · Maven · JUnit 5 + MockMvc

## Запуск
Требуется JDK 17+ и Maven 3.8+.

```bash
git clone <URL вашего репозитория>
cd muzify
mvn spring-boot:run
```
Приложение стартует на `http://localhost:8080`. По умолчанию используется встроенная БД **H2**
(в памяти), ничего настраивать не нужно. Консоль H2: `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:muzify`, user `sa`, пароль пустой).

### Запуск с PostgreSQL
1. Создайте базу: `CREATE DATABASE muzify;`
2. При необходимости поправьте логин/пароль в `src/main/resources/application-postgres.properties`
3. Запустите с профилем:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=postgres
   ```
Таблицы создаются автоматически (`spring.jpa.hibernate.ddl-auto=update`).

### Тесты
```bash
mvn test
```

## Тестовые пользователи (создаются при старте)
| Логин | Пароль | Роль |
|-------|--------|------|
| `admin` | `admin123` | ADMIN |
| `artist` | `artist123` | ARTIST |
| `user` | `user123` | USER |

Также создаются 5 демо-треков.

## Аутентификация
1. `POST /api/auth/login` → в ответе `token`
2. Во всех остальных запросах заголовок: `Authorization: Bearer <token>`

Без токена возвращается `401 {"code":401,"message":"Необходимо авторизоваться"}`.
Все ошибки имеют единый формат `{"code": ..., "message": "..."}`.

## API
| Метод | URL | Доступ | Описание |
|-------|-----|--------|----------|
| POST | `/api/auth/register` | все | регистрация (USER/ARTIST) |
| POST | `/api/auth/login` | все | вход, выдача JWT |
| GET | `/api/users/me` | авторизованные | профиль |
| GET | `/api/tracks?q=` | авторизованные | список / поиск треков |
| GET | `/api/tracks/{id}` | авторизованные | трек по id |
| POST | `/api/tracks` | ARTIST, ADMIN | добавить трек |
| PUT | `/api/tracks/{id}` | автор, ADMIN | изменить трек |
| DELETE | `/api/tracks/{id}` | автор, ADMIN | удалить трек |
| GET | `/api/favorites` | авторизованные | избранное |
| POST | `/api/favorites/{trackId}` | авторизованные | добавить в избранное (201 / 404 / 409) |
| DELETE | `/api/favorites/{trackId}` | авторизованные | удалить из избранного (204 / 404) |
| POST | `/api/listening` | авторизованные | начать прослушивание (201 / 400 / 404) |
| GET | `/api/listening/history` | авторизованные | история прослушиваний |
| GET | `/api/artist/stats` | ARTIST, ADMIN | прослушивания и избранное по своим трекам |
| GET | `/api/admin/users` | ADMIN | все пользователи |
| PATCH | `/api/admin/users/{id}/role` | ADMIN | сменить роль |
| GET | `/api/admin/stats` | ADMIN | общая статистика |

Готовые примеры запросов — в [`docs/api-examples.http`](docs/api-examples.http) (формат IntelliJ HTTP Client,
легко переносится в Postman).

## База данных
```
users (id, username*, email*, password, role, created_at)
tracks (id, title, artist, genre, duration_seconds, created_by -> users.id, created_at)
favorites (id, user_id -> users, track_id -> tracks, added_at)   UNIQUE(user_id, track_id)
listening_history (id, user_id -> users, track_id -> tracks, started_at)
```

## Структура проекта
```
src/main/java/com/muzify
 ├─ config/      SecurityConfig, DataInitializer
 ├─ security/    JwtService, JwtAuthFilter
 ├─ controller/  REST-контроллеры
 ├─ service/     бизнес-логика
 ├─ repository/  Spring Data JPA
 ├─ entity/      JPA-сущности
 ├─ dto/         запросы/ответы (records)
 └─ exception/   ApiException, GlobalExceptionHandler
docs/            презентация (PDF), примеры запросов
```
