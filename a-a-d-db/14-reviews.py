import pandas as pd
import numpy as np

# 1. Yorumları, temiz kullanıcılarımızı ve temiz ürünlerimizi okuyalım
reviews_df = pd.read_csv('db_reviews.csv')
users_df = pd.read_csv('last_version_users.csv') 
products_df = pd.read_csv('db_products_SENKRONIZE.csv') 

# 2. Yorum tablosunda olası kopya review_id varsa ezelim (Primary Key hatasına karşı)
reviews_df = reviews_df.drop_duplicates(subset=['review_id'])

# 3. Geçerli ve güncel ID'lerimizi listelere alalım
gecerli_user_idler = users_df['user_id'].tolist()
gecerli_urunler = products_df['product_id'].tolist()

# 4. Tüm yorumlara GÜNCEL kullanıcılarımızdan ve GÜNCEL ürünlerimizden rastgele birini atayalım
reviews_df['user_id'] = np.random.choice(gecerli_user_idler, size=len(reviews_df))
reviews_df['product_id'] = np.random.choice(gecerli_urunler, size=len(reviews_df))

# 5. E-ticaret sitemizin yepyeni ve hatasız yorum dosyasını kaydedelim!
reviews_df.to_csv('db_reviews_SENKRONIZE.csv', index=False)