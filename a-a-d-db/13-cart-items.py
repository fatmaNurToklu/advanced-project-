import pandas as pd
import numpy as np

# 1. Sepetlerimizi, ürünlerimizi ve orijinal cart_items dosyasını okuyalım
carts_df = pd.read_csv('db_carts_SENKRONIZE.csv')
products_df = pd.read_csv('db_products_SENKRONIZE.csv')
cart_items_df = pd.read_csv('db_cart_items.csv')

# 2. Olası PK kopyalarını silelim
cart_items_df = cart_items_df.drop_duplicates(subset=['cart_item_id'])

# 3. Güncel ID'leri listelere alalım
gecerli_sepetler = carts_df['cart_id'].tolist()
gecerli_urunler = products_df['product_id'].tolist()

# 4. Tüm sepet kalemlerine GÜNCEL sepetlerimizden ve GÜNCEL ürünlerimizden rastgele atayalım
cart_items_df['cart_id'] = np.random.choice(gecerli_sepetler, size=len(cart_items_df))
cart_items_df['product_id'] = np.random.choice(gecerli_urunler, size=len(cart_items_df))

# 5. Dosyamızı kaydedelim!
cart_items_df.to_csv('db_cart_items_SENKRONIZE.csv', index=False)
