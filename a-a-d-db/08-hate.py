import pandas as pd
import numpy as np

# 1. Dosyaları okuyalım
stores_df = pd.read_csv('active-stores.csv')
categories_df = pd.read_csv('db_categories.csv') 
products_df = pd.read_csv('db_products.csv')

# KRİTİK DOKUNUŞ: Aynı product_id'ye sahip kopyaları çöpe atıp sadece ilkini tutalım!
products_df = products_df.drop_duplicates(subset=['product_id'])

# 2. Elimizdeki geçerli ID'leri birer listeye alalım
gecerli_store_idler = stores_df['store_id'].tolist()
gecerli_category_idler = categories_df['category_id'].tolist()

# 3. Ürünleri rastgele dükkanlara ve kategorilere dağıtalım
products_df['store_id'] = np.random.choice(gecerli_store_idler, size=len(products_df))
products_df['category_id'] = np.random.choice(gecerli_category_idler, size=len(products_df))

# 4. Ve tertemiz dosyamızı kaydedelim!
products_df.to_csv('db_products_SENKRONIZE.csv', index=False)