import pandas as pd

# 1. Geçerli ürünlerimizi ve orijinal envanteri okuyalım
products_df = pd.read_csv('db_products_SENKRONIZE.csv')
inventory_df = pd.read_csv('db_inventory.csv')

# 2. Envanter tablosundaki olası kopya ID'leri temizleyelim (Primary Key hatasını baştan önleyelim)
inventory_df = inventory_df.drop_duplicates(subset=['inventory_id'])

# 3. İki tablonun satır sayısını eşitleyelim (Her ürüne 1 depo kaydı düşecek şekilde)
gecerli_urunler = products_df['product_id'].tolist()
min_satir = min(len(gecerli_urunler), len(inventory_df))

inventory_df = inventory_df.head(min_satir).copy()

# 4. Geçerli ürün ID'lerimizi envanter tablosuna yapıştıralım!
inventory_df['product_id'] = gecerli_urunler[:min_satir]

# 5. Tertemiz dosyamızı kaydedelim!
inventory_df.to_csv('db_inventory_SENKRONIZE.csv', index=False)