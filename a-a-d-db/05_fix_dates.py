import pandas as pd

print("Kapsamlı veri temizliği başlıyor, lütfen bekleyin...")

# 1. Dosyaları güvenli bir şekilde oku (Uyarıları engellemek için low_memory=False)
df = pd.read_csv('db_users.csv', low_memory=False)
dc = pd.read_csv('db_customer_profiles.csv', low_memory=False)


# ==========================================
# AŞAMA 1: DB_USERS TEMİZLİĞİ
# ==========================================
print("Users tablosu temizleniyor...")
# Tarih formatlarını düzelt
df['created_at'] = pd.to_datetime(df['created_at']).dt.strftime('%Y-%m-%d %H:%M:%S')
df['updated_at'] = pd.to_datetime(df['updated_at']).dt.strftime('%Y-%m-%d %H:%M:%S')

# Boolean (True/False) düzeltmesi
if 'status' in df.columns:
    df['status'] = df['status'].astype(str).str.lower()


# ==========================================
# AŞAMA 2: DB_CUSTOMER_PROFILES TEMİZLİĞİ
# ==========================================
print("Customer Profiles tablosu temizleniyor...")
# Tarih formatını düzelt
if 'last_login' in dc.columns:
    dc['last_login'] = pd.to_datetime(dc['last_login']).dt.strftime('%Y-%m-%d %H:%M:%S')

# Total Spend (Harcama) sütunundaki $ veya , işaretlerini temizle
if 'total_spend' in dc.columns:
    dc['total_spend'] = dc['total_spend'].astype(str).str.replace('$', '', regex=False).str.replace(',', '', regex=False)
    dc['total_spend'] = pd.to_numeric(dc['total_spend'], errors='coerce') 

# Satisfaction Level ve Age sütunlarındaki harfleri/bozuklukları temizle (Tam Sayı - Integer'a zorla)
if 'satisfaction_level' in dc.columns:
    dc['satisfaction_level'] = pd.to_numeric(dc['satisfaction_level'], errors='coerce')

if 'age' in dc.columns:
    dc['age'] = pd.to_numeric(dc['age'], errors='coerce').astype('Int64')


# ==========================================
# AŞAMA 3: KAYDETME VE BİTİŞ
# ==========================================
df.to_csv('db_users.csv', index=False)
dc.to_csv('db_customer_profiles.csv', index=False)

print("✅ Tüm tarihler, bozuk sayılar ve mantıksal (Boolean) değerler başarıyla temizlendi!")
print("🚀 Artık DBeaver üzerinden sorunsuz bir şekilde Import edebilirsin.")