import psycopg2
import bcrypt

conn = psycopg2.connect(
    host="localhost",
    port=5432,
    database="ecommerce_db",
    user="postgres",
    password="admin123"
)
cur = conn.cursor()

cur.execute("SELECT user_id, email FROM users")
users = cur.fetchall()

new_password = "Test1234"
hashed = bcrypt.hashpw(new_password.encode("utf-8"), bcrypt.gensalt(10)).decode("utf-8")

cur.execute("UPDATE users SET password_hash = %s", (hashed,))
conn.commit()

print(f"Toplam {len(users)} kullanıcının şifresi güncellendi.")
print(f"Yeni şifre: {new_password}")
print("Örnek kullanıcılar:")
for uid, email in users[:5]:
    print(f"  {email} → şifre: {new_password}")

cur.close()
conn.close()
