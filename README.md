# Система управления задачами

## Инструкция по установке

### 1. Клонируйте репозиторий
Для начала клонируйте репозиторий с помощью следующей команды:
```git clone https://github.com/S0IG0/task-managment.git```

### 2. Перейдите в каталог проекта
Перейдите в каталог проекта с помощью следующей команды:
```cd task-managment```

### 3. Запустите контейнеры
Запустить все контейнеры с помощью следующей команды:
```docker-compose up -d```

## Инструкция по использованию 

После того как все контейнеры будут запущены, вы сможете получить доступ к документации Swagger OpenAPI по следующему адресу:
```http://localhost:8081/swagger-ui/index.html```

## Пользователи
В системе уже созданы следующие пользователи:

- **Пользователь с правами администратора:**
    - Почта: `admin@mail.ru`
    - Пароль: `password`

- **Другие пользователи:**
    - Почта: `user1@mail.ru`, Пароль: `password`
    - Почта: `user2@mail.ru`, Пароль: `password`
    - Почта: `user3@mail.ru`, Пароль: `password`

## Задачи и комментарии

В системе уже имеются примеры задач с комментариями. Вы можете использовать Swagger API для работы с ними, создавая, редактируя и удаляя задачи и комментарии.

# Аутентификация с помощью JWT

Так как запросы аутентифицированы с помощью JWT токена, вам нужно его получить.

### Получение JWT токена

Для того чтобы получить токен, отправьте POST запрос на `/api/auth/login` с данными администратора:

```json
{
  "email": "admin@mail.ru",
  "password": "password"
}
```

После выполнения запроса вы получите следующий ответ:

```json
{
  "access": "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI3M2QzMjgyMy1kMDI0LTQxMmEtYjA5NC1iMmE2ZDA0NDcyYTkiLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sInRva2VuVHlwZSI6IkFDQ0VTUyIsInVzZXJJZCI6ImZlMzc3ZjVlLWZiNTgtNDczMi1iZmYzLTYzY2U4OWUwMGVkYSIsInN1YiI6ImFkbWluIiwiaWF0IjoxNzMxODkwNTIwLCJleHAiOjE3MzE4OTQxMjB9.DqUqXVgis3tnAdMks0TKG6ou0POF4_eEmDRQfv5wgKzsfIobHIQ20opK3sLCc6DBgxwbc3jgljYxlP0uzOHmzQ",
  "refresh": "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI3M2QzMjgyMy1kMDI0LTQxMmEtYjA5NC1iMmE2ZDA0NDcyYTkiLCJ0b2tlblR5cGUiOiJSRUZSRVNIIiwic3ViIjoiYWRtaW4iLCJpYXQiOjE3MzE4OTA1MjAsImV4cCI6MTczMTg5NDEyMH0.wJSjPQ1LmD-0MDPHYiVaVpnLleaWRiCGRCYypzXqVhn3gjceo26HHhMbMDYEwKrGbwSVGw4-NRPaMa6_yc5ogg"
}
```

### Авторизация в Swagger

1. Скопируйте содержимое поля `access`.
2. Нажмите на кнопку **Authorize** в правом верхнем углу интерфейса Swagger.
3. Вставьте содержимое `access` в поле `Value` и нажмите кнопку **Authorize**.

Поздравляю! Теперь вы можете получить доступ к endpoint, которые защищены JWT токеном.

# Создание задач

При создании задачи обязательно указывается **ID пользователя**, который будет исполнителем этой задачи. Убедитесь, что вы правильно указали ID пользователя, чтобы задача была назначена корректному исполнителю.