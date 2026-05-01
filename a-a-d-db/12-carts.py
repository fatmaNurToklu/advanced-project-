import pandas as pd
import numpy as np

# 1. Orijinal sepetleri ve tertemiz kullanıcılarımızı okuyalım
carts_df = pd.read_csv('db_carts.csv')
users_df = pd.read_csv('last_version_users.csv')

# 2. Sepet tablosunda kopya cart_id varsa ezelim (Primary Key hatasına karşı)
carts_df = carts_df.drop_duplicates(subset=['cart_id'])

# 3. Geçerli kullanıcı ID'lerimizi bir listeye alalım
gecerli_user_idler = users_df['user_id'].tolist()

# 4. Tüm sepetlere GÜNCEL kullanıcılarımızdan rastgele birini atayalım
carts_df['user_id'] = np.random.choice(gecerli_user_idler, size=len(carts_df))

# 5. E-ticaret sitemizin yepyeni sepet dosyasını kaydedelim!
carts_df.to_csv('db_carts_SENKRONIZE.csv', index=False)