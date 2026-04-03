import requests
import time

base = "http://localhost:8000"
print("тестируем сервис...\n")

required_fields = {"status", "message", "steamId"}

print("1 создаем привязку")
r = requests.post(
    f"{base}/bind",
    json={
        "telegramId": 123,
        "steamLink": "https://steamcommunity.com/profiles/76561197960435530/",
    },
)
print(f"статус: {r.status_code}")
response_json = r.json()
print(f"ответ: {response_json}\n")
print(f"контракт bind: {required_fields.issubset(response_json.keys())}")
time.sleep(1)

print("2 получаем steam id")
r = requests.get(f"{base}/link/123")
print(f"статус: {r.status_code}")
print(f"ответ: {r.json()}\n")
time.sleep(1)

print("3 пробуем тот же telegram")
r = requests.post(
    f"{base}/bind",
    json={
        "telegramId": 123,
        "steamLink": "https://steamcommunity.com/profiles/76561197960435531/",
    },
)
print(f"статус: {r.status_code}")
response_json = r.json()
print(f"ответ: {response_json}\n")
print(f"контракт bind: {required_fields.issubset(response_json.keys())}")
time.sleep(1)

print("4 пробуем тот же steam")
r = requests.post(
    f"{base}/bind",
    json={
        "telegramId": 456,
        "steamLink": "https://steamcommunity.com/profiles/76561197960435530/",
    },
)
print(f"статус: {r.status_code}")
response_json = r.json()
print(f"ответ: {response_json}\n")
print(f"контракт bind: {required_fields.issubset(response_json.keys())}")
time.sleep(1)

print("5 неправильная ссылка")
r = requests.post(
    f"{base}/bind",
    json={
        "telegramId": 789,
        "steamLink": "https://steamcommunity.com/id/vasya/",
    },
)
print(f"статус: {r.status_code}")
response_json = r.json()
print(f"ответ: {response_json}\n")
print(f"контракт bind: {required_fields.issubset(response_json.keys())}")
time.sleep(1)

print("6 несуществующий telegram")
r = requests.get(f"{base}/link/999")
print(f"статус: {r.status_code}")
print(f"ответ: {r.json()}")
