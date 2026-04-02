import requests
import time

base = "http://localhost:8000"
print("тестируем сервис...\n")

# 1. создаём привязку
print("1. создаём привязку")
r = requests.post(f"{base}/bind", json={
    "telegram_id": 123,
    "steam_link": "https://steamcommunity.com/profiles/76561197960435530/"
})
print(f"статус: {r.status_code}")
print(f"ответ: {r.text}\n")
time.sleep(1)

# 2. получаем steam id
print("2. получаем steam id")
r = requests.get(f"{base}/link/123")
print(f"статус: {r.status_code}")
print(f"ответ: {r.json()}\n")
time.sleep(1)

# 3. пробуем привязать тот же telegram (ошибка)
print("3. пробуем тот же telegram")
r = requests.post(f"{base}/bind", json={
    "telegram_id": 123,
    "steam_link": "https://steamcommunity.com/profiles/76561197960435531/"
})
print(f"статус: {r.status_code}")
print(f"ответ: {r.text}\n")
time.sleep(1)

# 4. пробуем привязать тот же steam (ошибка)
print("4. пробуем тот же steam")
r = requests.post(f"{base}/bind", json={
    "telegram_id": 456,
    "steam_link": "https://steamcommunity.com/profiles/76561197960435530/"
})
print(f"статус: {r.status_code}")
print(f"ответ: {r.text}\n")
time.sleep(1)

# 5. неправильная ссылка (ошибка)
print("5. неправильная ссылка")
r = requests.post(f"{base}/bind", json={
    "telegram_id": 789,
    "steam_link": "https://steamcommunity.com/id/vasya/"
})
print(f"статус: {r.status_code}")
print(f"ответ: {r.text}\n")
time.sleep(1)

# 6. проверяем несуществующий telegram
print("6. несуществующий telegram")
r = requests.get(f"{base}/link/999")
print(f"статус: {r.status_code}")
print(f"ответ: {r.json()}")