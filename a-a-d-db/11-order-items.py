import pandas as pd
import numpy as np

# 1. Tertemiz siparişlerimizi, ürünlerimizi ve orijinal order_items dosyasını okuyalım
orders_df = pd.read_csv('db_orders_SENKRONIZE.csv')
products_df = pd.read_csv('db_products_SENKRONIZE.csv')
order_items_df = pd.read_csv('db_order_items.csv')

# 2. Olası kopyaları silerek Primary Key (order_item_id) hatasını baştan önleyelim
order_items_df = order_items_df.drop_duplicates(subset=['order_item_id'])

# 3. Güncel ve geçerli ID'leri listelere alalım
gecerli_siparisler = orders_df['order_id'].tolist()
gecerli_urunler = products_df['product_id'].tolist()

# 4. Tüm sipariş kalemlerine, GÜNCEL siparişlerimizden ve GÜNCEL ürünlerimizden rastgele birer tane atayalım
order_items_df['order_id'] = np.random.choice(gecerli_siparisler, size=len(order_items_df))
order_items_df['product_id'] = np.random.choice(gecerli_urunler, size=len(order_items_df))

# 5. E-ticaret sitemizin son ve en sağlam dosyasını kaydedelim!
order_items_df.to_csv('db_order_items_SENKRONIZE.csv', index=False)