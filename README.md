# Тестирование канбан-доски с Selenium (Java)

[![hexlet-check](https://github.com/mikitasazan/qa-auto-engineer-java-project-385/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/mikitasazan/qa-auto-engineer-java-project-385/actions)

Научитесь проектировать и поддерживать UI-тесты на Java с использованием Selenium WebDriver и JUnit.

Учебный проект Хекслета: https://ru.hexlet.io/programs/qa-auto-engineer-java


## Стек

- Java

## Установка

Требуется JDK 21+, Docker и Google Chrome. Драйвер Chrome подбирается Selenium Manager автоматически.

```bash
git clone https://github.com/mikitasazan/qa-auto-engineer-java-project-385.git
cd qa-auto-engineer-java-project-385
make install
```

## Использование

Тестируемое приложение поднимается отдельным контейнером на порту 5173:

```bash
make start
```

В другом терминале — прогон тестов и проверка стиля:

```bash
make test        # ./gradlew test
make lint        # ./gradlew spotlessCheck
make lint-fix    # ./gradlew spotlessApply
```

Остановить приложение: `make stop`.

### Что покрыто тестами

| Набор | Что проверяет |
|---|---|
| `ApplicationSmokeTest` | приложение поднимается, отдаёт страницу входа |
| `AuthenticationTest` | вход с верными и неверными данными, выход |
| `UsersManagementTest` | CRUD пользователей |
| `StatusesManagementTest` | CRUD статусов задач |
| `LabelsManagementTest` | CRUD меток |
| `KanbanBoardTest` | канбан-доска: фильтрация, смена статуса, карточки задач |

Инфраструктура тестов: `pages/` — Page Object на каждый экран, `support/` — фабрика браузера, локаторы и логирование, `config/` — учётные данные и настройки прогона.

---

<details>
<summary>Автоматические тесты Хекслета</summary>

Тесты запускаются на каждый коммит. За запуск отвечает файл `.github/workflows/hexlet-check.yml` — не удаляйте и не переименовывайте ни его, ни репозиторий.

</details>

## О Хекслете

[Хекслет](https://ru.hexlet.io/) — школа программирования: авторские программы обучения с практикой, поддержкой наставников и реальными проектами, которые остаются в резюме. Этот репозиторий — один из таких проектов.
