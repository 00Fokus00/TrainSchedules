# Railway Schedules System

REST API приложение для управления железнодорожными операциями: станциями, поездами, локомотивами, расписаниями и остановками.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-✓-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Docker](https://img.shields.io/badge/Docker-✓-blue)

## ✨ Функциональность

Приложение предоставляет полный набор операций для управления железнодорожной инфраструктурой:

| Модуль | Описание |
|--------|----------|
| **Stations** | Управление железнодорожными станциями |
| **Trains** | Управление поездами |
| **Locomotives** | Управление локомотивами |
| **Schedules** | Создание и редактирование расписаний поездов |
| **Carriages** | Управление вагонами |
| **Station Entries** | Управление остановками поездов на станциях |

### Возможности системы

- 📅 Создание расписаний с указанием времени отправления и прибытия
- 🚉 Добавление промежуточных остановок с порядковыми номерами
- 🔍 Фильтрация и поиск записей по множеству параметров
- 📝 CRUD-операции для всех сущностей
- 📊 Пагинация и сортировка данных

## 🛠 Технологический стек

- **Backend:** Java 17, Spring Boot         
- **База данных:** PostgreSQL 15
- **Контейнеризация:** Docker, Docker Compose
- **Сборка:** Maven
## Скриншоты
| ![RailwayScheduleSystem](RailwaySchedulesSystem.png) |
|:--:|
| *Главная страница* |

| ![EditSchedule](EditSchedule.png) |
|:--:|
| *Редактирование станции* |

| ![StationEntries](StationEntries.png) |
|:--:|
| *Список станций* |
