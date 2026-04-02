import sqlite3

conn = sqlite3.connect("database.db")
c = conn.cursor()

print("таблица link:")
print("-" * 50)

c.execute("SELECT * FROM link")
rows = c.fetchall()

if not rows:
    print("пусто")
else:
    for row in rows:
        print(f"id: {row[0]}, telegram: {row[1]}, steam: {row[2]}")

conn.close()