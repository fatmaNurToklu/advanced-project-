import pandas as pd

print("🔍 Adresler ve Kullanıcılar tablosu DERİNLEMESİNE karşılaştırılıyor...")

users = pd.read_csv('db_users.csv', low_memory=False)
addresses = pd.read_csv('db_user_addresses.csv', low_memory=False)

# TUZAK 1'İ BOZ: Tüm ID'lerdeki görünmez boşlukları (space) zorla sil
users['user_id'] = users['user_id'].astype(str).str.strip()
addresses['user_id'] = addresses['user_id'].astype(str).str.strip()

# Karşılaştırma yap ve sadece geçerli olanları tut
valid_user_ids = set(users['user_id'])
valid_addresses = addresses[addresses['user_id'].isin(valid_user_ids)]

deleted_count = len(addresses) - len(valid_addresses)
print(f"👻 Silinen hayalet adres sayısı: {deleted_count}")

# TUZAK 2'Yİ BOZ: Dosyayı YENİ BİR İSİMLE kaydet (DBeaver eski dosyayı unutsun)
valid_addresses.to_csv('db_user_addresses_TEMIZ.csv', index=False, na_rep='\\N')

print("✅ Tertemiz yeni dosya 'db_user_addresses_TEMIZ.csv' adıyla oluşturuldu!")