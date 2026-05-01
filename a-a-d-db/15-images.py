import pandas as pd
import numpy as np

# 1. Orijinal görselleri ve tertemiz ürünlerimizi okuyalım
images_df = pd.read_csv('db_product_images.csv')
products_df = pd.read_csv('db_products_SENKRONIZE.csv')

# 2. ŞU ANKİ HATANIN ÇÖZÜMÜ: Kopya görsel ID'lerini çöpe atıyoruz (Primary Key koruması)
images_df = images_df.drop_duplicates(subset=['image_id'])

# 3. GİZLİ TEHLİKENİN ÇÖZÜMÜ: Görsellere GÜNCEL ürünlerimizden rastgele atayalım (Foreign Key koruması)
gecerli_urunler = products_df['product_id'].tolist()
images_df['product_id'] = np.random.choice(gecerli_urunler, size=len(images_df))

# 4. Kusursuz dosyamızı kaydedelim!
images_df.to_csv('db_product_images_SENKRONIZE.csv', index=False)