# Облачное хранилище

REST-сервис для загрузки файлов и вывода списка уже загруженных файлов пользователя.  
Полностью соответствует спецификации [Cloud API (OpenAPI)](https://github.com/netology-code/jd-homeworks/blob/master/diploma/cloudservice.md)

## Запуск проекта



```bash
# Скачивание зависимостей и сборка образа
docker-compose up --build

# Или в фоне
docker-compose up -d --build
Приложение будет доступно по адресу:
http://localhost:8080/cloud
База данных PostgreSQL — http://localhost:5432

Тестовые пользователи

Логин           Пароль      Роль
user            password    Пользователь
alice           alice123    Пользователь
admin           secret  Администратор

Подключение фронтенда

Скачать фронтенд:
https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend
В папке фронтенда создать/отредактировать файл .env:envVUE_APP_BASE_URL=http://localhost:8080/cloud
Запустить:Bashnpm install
npm run serve

Фронтенд будет доступен по адресу: http://localhost:8081

API (основные эндпоинты)
BashPOST   /cloud/login     → { "auth-token": "..." }
POST   /cloud/logout    → (с заголовком auth-token)
GET    /cloud/list?limit=10
POST   /cloud/file?filename=...
GET    /cloud/file?filename=...
PUT    /cloud/file?filename=...  → { "filename": "new-name.jpg" }
DELETE /cloud/file?filename=...
Тесты
Bash# Unit + интеграционные тесты с Testcontainers
./gradlew test
