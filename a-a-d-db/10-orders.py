import pandas as pd
import numpy as np

# 1. Siparişleri, güncel kullanıcılarımızı ve güncel dükkanlarımızı okuyalım
orders_df = pd.read_csv('db_orders.csv')
users_df = pd.read_csv('last_version_users.csv') # Temiz kullanıcılarımız
stores_df = pd.read_csv('active-stores.csv')     # Ayakta kalan dükkanlarımız

# 2. Sipariş tablosunda olası kopya order_id'ler varsa ezelim (Primary Key hatasına karşı)
orders_df = orders_df.drop_duplicates(subset=['order_id'])

# 3. Geçerli ID'leri birer listeye alalım
gecerli_user_idler = users_df['user_id'].tolist()
gecerli_store_idler = stores_df['store_id'].tolist()

# 4. Tüm siparişlere rastgele GÜNCEL kullanıcı ve GÜNCEL dükkan atayalım
orders_df['user_id'] = np.random.choice(gecerli_user_idler, size=len(orders_df))
orders_df['store_id'] = np.random.choice(gecerli_store_idler, size=len(orders_df))

# 5. Ve tertemiz dosyamızı kaydedelim!
orders_df.to_csv('db_orders_SENKRONIZE.csv', index=False)