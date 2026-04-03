# auth_py

Python-микросервис для привязки Telegram и Steam аккаунтов.

## Подготовка

```bash
cd auth_py
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

## Миграции базы данных

Перед запуском сервиса сначала применяйте миграции Alembic.

```bash
cd auth_py
alembic upgrade head
```

Если нужно откатить последнюю миграцию:

```bash
cd auth_py
alembic downgrade -1
```

## Запуск сервиса

После применения миграций:

```bash
cd auth_py
uvicorn main:app --host 0.0.0.0 --port 8001 --reload
```

## Полезные команды Alembic

Создать новую миграцию вручную:

```bash
cd auth_py
alembic revision -m "описание изменений"
```

Создать миграцию по изменениям моделей:

```bash
cd auth_py
alembic revision --autogenerate -m "описание изменений"
```

Проверить текущую версию миграций:

```bash
cd auth_py
alembic current
```
