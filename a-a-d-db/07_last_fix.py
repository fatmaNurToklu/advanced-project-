import pandas as pd

# 1. Dışarı aktardığımız geçerli kullanıcıları ve orjinal profilleri oku
# (Terminal ana klasörde olduğu için alt klasörün adını yola ekledik)
users_df = pd.read_csv('a-a-d-db/last_version_users.csv')
profiles_df = pd.read_csv('a-a-d-db/db_customer_profiles.csv')

# 2. Geçerli user_id'leri bir listeye al
guncel_id_listesi = users_df['user_id'].tolist()

# 3. Profil sayısını, elimizdeki gerçek kullanıcı sayısına eşitle (ne eksik ne fazla)
profiles_df = profiles_df.head(len(guncel_id_listesi)).copy()

# 4. Profillerin user_id sütununu, bizim geçerli ID'lerimizle tamamen değiştir
profiles_df['user_id'] = guncel_id_listesi

# 5. Yeni ve tertemiz dosyamızı aynı alt klasöre kaydedelim!
profiles_df.to_csv('a-a-d-db/db_customer_profiles_SENKRONIZE.csv', index=False)

addresses_df = pd.read_csv('a-a-d-db/db_user_addresses.csv')

# 2. Geçerli user_id'leri bir listeye al
guncel_id_listesi = users_df['user_id'].tolist()
# 1. Orjinal adres dosyasını oku
addresses_df = pd.read_csv('a-a-d-db/db_user_addresses.csv')

# 2. Adres tablomuzda kaç satır varsa (7519), güncel ID listemizden o kadar kişi seçelim
adres_sayisi = len(addresses_df)
secilen_idler = guncel_id_listesi[:adres_sayisi] # Sadece ilk 7519 ID'yi kopardık

# 3. Adreslerin user_id sütununu, bu seçtiğimiz ID'lerle değiştirelim
addresses_df['user_id'] = secilen_idler

# 4. Yeni ve tertemiz dosyamızı aynı alt klasöre kaydedelim!
addresses_df.to_csv('a-a-d-db/db_user_addresses_SENKRONIZE.csv', index=False)
