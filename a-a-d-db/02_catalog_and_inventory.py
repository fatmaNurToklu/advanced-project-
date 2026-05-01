import pandas as pd
import uuid
import random
import urllib.parse

def generate_catalog_layer():
    print("🚀 2. Katman: Dinamik Mağaza ve Emoji Destekli Katalog Eşleştirme Başladı...")

    try:
        # Tüm veri setlerini ve 1. Katmanda oluşturduğumuz kullanıcıları yüklüyoruz
        df_retail = pd.read_csv('cleaned_online_retail.csv')
        df_amazon = pd.read_csv('cleaned_amazon_sales.csv')
        df_pakistan = pd.read_csv('cleaned_pakistan_ecommerce.csv', low_memory=False)
        df_reviews = pd.read_csv('cleaned_amazon_reviews.csv', sep='\t')
        df_users = pd.read_csv('db_users.csv') # YENİ: Corporate kullanıcılar için
    except FileNotFoundError as e:
        print(f"❌ Hata: Dosyalar bulunamadı!\n{e}")
        return

    stores_list = []
    categories_list = []
    products_list = []
    product_images_list = []

    # ==========================================
    # 1. STORES (Dinamik CORPORATE Kullanıcılarından)
    # ==========================================
    corporate_users = df_users[df_users['role'] == 'CORPORATE']
    
    if corporate_users.empty:
        print("❌ Hata: db_users.csv içinde CORPORATE kullanıcı yok!")
        return

    store_suffixes = ["Global", "Market", "Hub", "Boutique", "Express", "Store", "Emporium"]
    
    for _, user in corporate_users.iterrows():
        # Corporate kullanıcısının adına göre mağaza oluştur
        stores_list.append({
            'store_id': f"STORE_{uuid.uuid4().hex[:6].upper()}",
            'owner_id': user['user_id'],
            'store_name': f"{user['first_name']} {random.choice(store_suffixes)}",
            'description': f"Official store operated by {user['first_name']} {user['last_name']}",
            'status': 'Open',
            'store_rating': round(random.uniform(3.5, 5.0), 1),
            'created_at': '2025-01-01 10:00:00'
        })
        
    store_ids = [s['store_id'] for s in stores_list]
    print(f"🏪 {len(stores_list)} adet Corporate mağaza oluşturuldu.")

    # ==========================================
    # 2. CATEGORIES (Emoji Destekli Hiyerarşik Yapı)
    # ==========================================
    parent_categories = {
        'Electronics & Gadgets': {'id': 'CAT_P1', 'emoji': '💻'},
        'Fashion & Apparel': {'id': 'CAT_P2', 'emoji': '👕'},
        'Home & Lifestyle': {'id': 'CAT_P3', 'emoji': '🛋️'},
        'Beauty & Health': {'id': 'CAT_P4', 'emoji': '✨'},
        'General Retail': {'id': 'CAT_P5', 'emoji': '🛍️'},
        'Books & Media': {'id': 'CAT_P6', 'emoji': '📚'}
    }

    # Parent kategorileri listeye ekle
    for name, data in parent_categories.items():
        categories_list.append({
            'category_id': data['id'], 
            'parent_id': None, 
            'name': name,
            'slug': name.lower().replace(' & ', '-').replace(' ', '-'),
            'icon_emoji': data['emoji'] # URL yerine Emoji
        })

    def assign_emoji_and_parent(cat_name):
        """Kategori ismine bakarak hem Parent ID hem de mantıklı bir Emoji seçer."""
        cat_lower = str(cat_name).lower()
        
        # Electronics
        if any(kw in cat_lower for kw in ['mobile', 'phone', 'cell']): return 'CAT_P1', '📱'
        elif any(kw in cat_lower for kw in ['audio', 'headphone', 'music', 'speaker']): return 'CAT_P1', '🎧'
        elif any(kw in cat_lower for kw in ['computer', 'laptop', 'pc', 'tablet']): return 'CAT_P1', '💻'
        elif any(kw in cat_lower for kw in ['tv', 'television', 'video']): return 'CAT_P1', '📺'
        elif any(kw in cat_lower for kw in ['appliance', 'electronic', 'cable']): return 'CAT_P1', '🔌'
        
        # Fashion & Apparel
        elif any(kw in cat_lower for kw in ['men', 'women', 'clothing', 'shirt', 'dress', 'fashion']): return 'CAT_P2', '👗'
        elif any(kw in cat_lower for kw in ['shoes', 'sneaker', 'boot']): return 'CAT_P2', '👟'
        elif any(kw in cat_lower for kw in ['watch', 'jewelry', 'accessory', 'ring']): return 'CAT_P2', '⌚'
        
        # Home & Lifestyle
        elif any(kw in cat_lower for kw in ['kitchen', 'cooking', 'dining', 'food']): return 'CAT_P3', '🍳'
        elif any(kw in cat_lower for kw in ['furniture', 'sofa', 'bed']): return 'CAT_P3', '🛏️'
        elif any(kw in cat_lower for kw in ['home', 'decor', 'living']): return 'CAT_P3', '🏡'
        
        # Beauty & Health
        elif any(kw in cat_lower for kw in ['beauty', 'makeup', 'cosmetic']): return 'CAT_P4', '💄'
        elif any(kw in cat_lower for kw in ['health', 'care', 'skin', 'bath']): return 'CAT_P4', '🧴'
        
        # Books & Media
        elif any(kw in cat_lower for kw in ['book', 'novel', 'reading']): return 'CAT_P6', '📖'
        elif any(kw in cat_lower for kw in ['movie', 'dvd', 'film']): return 'CAT_P6', '🎬'
        elif any(kw in cat_lower for kw in ['toy', 'game', 'play']): return 'CAT_P6', '🎮'
        
        # General / Default
        else: return 'CAT_P5', '📦'

    # Tüm veri setlerindeki raw kategorileri topla
    amz_cats = df_amazon['Category'].dropna().unique().tolist()
    pak_cats = df_pakistan['category_name_1'].dropna().unique().tolist()
    rev_cats = df_reviews['product_category'].dropna().unique().tolist()
    
    all_raw_categories = list(set(amz_cats + pak_cats + rev_cats))
    cat_mapping = {} 
    
    # Sub-kategorileri listeye ekle
    for idx, cat_name in enumerate(all_raw_categories, 1):
        child_id = f"CAT_C{idx}"
        cat_mapping[cat_name] = child_id
        parent_id, emoji = assign_emoji_and_parent(cat_name) # Fonksiyonu iki değer dönecek şekilde kullanıyoruz
        
        categories_list.append({
            'category_id': child_id, 
            'parent_id': parent_id, 
            'name': str(cat_name).title(),
            'slug': str(cat_name).lower().replace(' ', '-').replace('&', 'and'),
            'icon_emoji': emoji # URL yerine Emoji
        })

    # ==========================================
    # 3. PRODUCTS (SADECE GERÇEK VERİLER)
    # ==========================================
    print("⏳ Ürünler gerçek verilerle birleştiriliyor ve mağazalara dağıtılıyor...")

    # A. Amazon Sales
    for _, row in df_amazon.drop_duplicates(subset=['SKU']).iterrows():
        qty = float(row['Qty']) if pd.notnull(row['Qty']) and float(row['Qty']) > 0 else 1.0
        amount = float(row['Amount']) if pd.notnull(row['Amount']) else 0.0
        real_unit_price = round(amount / qty, 2)
        products_list.append({
            'product_id': f"PROD_{uuid.uuid4().hex[:8].upper()}", 
            'store_id': random.choice(store_ids), 
            'category_id': cat_mapping.get(row['Category'], 'CAT_P5'), 
            'sku': f"AMZ_{row['SKU']}",
            'name': str(row.get('Style', 'Amazon Item')) + " - " + str(row['Category']),
            'description': f"Size: {row.get('Size', 'N/A')} | ASIN: {row.get('ASIN', 'N/A')}", 
            'base_price': real_unit_price, 
            'cost_of_product': round(real_unit_price * 0.6, 2), 
            'is_active': True
        })

    # B. Pakistan Ürünleri
    for _, row in df_pakistan.drop_duplicates(subset=['sku']).dropna(subset=['sku']).iterrows():
        real_price = float(row['price']) if pd.notnull(row['price']) else 0.0
        products_list.append({
            'product_id': f"PROD_{uuid.uuid4().hex[:8].upper()}", 
            'store_id': random.choice(store_ids), 
            'category_id': cat_mapping.get(row['category_name_1'], 'CAT_P5'), 
            'sku': f"PAK_{row['sku']}",
            'name': str(row['sku']).replace('-', ' ').title()[:50], 
            'description': f"Category: {row['category_name_1']} | Pakistan Dataset", 
            'base_price': real_price, 
            'cost_of_product': round(real_price * 0.6, 2), 
            'is_active': True
        })

    # C. Online Retail Ürünleri
    for _, row in df_retail.drop_duplicates(subset=['StockCode']).dropna(subset=['StockCode']).iterrows():
        real_price = float(row['UnitPrice']) if pd.notnull(row['UnitPrice']) else 0.0
        products_list.append({
            'product_id': f"PROD_{uuid.uuid4().hex[:8].upper()}", 
            'store_id': random.choice(store_ids), 
            'category_id': 'CAT_P5', 
            'sku': f"RET_{row['StockCode']}",
            'name': str(row['Description']).title(), 
            'description': str(row['Description']).title(), 
            'base_price': real_price, 
            'cost_of_product': round(real_price * 0.6, 2), 
            'is_active': True
        })

    # D. Amazon Reviews Ürünleri
    for _, row in df_reviews.drop_duplicates(subset=['product_id']).dropna(subset=['product_id']).iterrows():
        assigned_price = round(random.uniform(9.99, 49.99), 2)
        products_list.append({
            'product_id': f"PROD_{uuid.uuid4().hex[:8].upper()}", 
            'store_id': random.choice(store_ids), 
            'category_id': cat_mapping.get(row['product_category'], 'CAT_P6'), 
            'sku': f"REV_{row['product_id']}",
            'name': str(row['product_title'])[:100], 
            'description': f"Category: {row['product_category']} | Review Dataset", 
            'base_price': assigned_price, 
            'cost_of_product': round(assigned_price * 0.6, 2), 
            'is_active': True
        })

    # ==========================================
    # 4. PRODUCT IMAGES (SVG Emoji Yer Tutucuları)
    # ==========================================
    print("📸 Ürün fotoğrafları SVG Emoji formatında hazırlanıyor...")
    
    # Hızlı erişim için kategori emojilerini mapliyoruz
    category_emojis = {c['category_id']: c['icon_emoji'] for c in categories_list}
    
    for prod in products_list:
        # Ürünün kategorisine ait emojiyi bul (bulamazsa kutu emojisi)
        cat_id = prod['category_id']
        emoji = category_emojis.get(cat_id, "📦")
        
        # Emojiyi içeren, şık, yuvarlak hatlı bir SVG görseli oluşturuyoruz
        svg_code = f"""<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400">
            <rect width="100%" height="100%" fill="#f4f6f8" rx="30"/>
            <text x="50%" y="50%" font-size="180" text-anchor="middle" dominant-baseline="central">{emoji}</text>
        </svg>"""
        
        # SVG kodunu URL formatına (Data URI) çeviriyoruz
        svg_data_url = "data:image/svg+xml;utf8," + urllib.parse.quote(svg_code)

        product_images_list.append({
            'image_id': f"IMG_{uuid.uuid4().hex[:8].upper()}",
            'product_id': prod['product_id'],
            'image_url': svg_data_url, # Artık dış link değil, saf SVG kodu!
            'is_primary': True,
            'display_order': 1,
            'created_at': '2025-01-01 10:00:00'
        })
        
    # ==========================================
    # CSV KAYDETME
    # ==========================================
    pd.DataFrame(stores_list).to_csv('db_stores.csv', index=False)
    pd.DataFrame(categories_list).to_csv('db_categories.csv', index=False)
    pd.DataFrame(products_list).to_csv('db_products.csv', index=False)
    pd.DataFrame(product_images_list).to_csv('db_product_images.csv', index=False)

    print(f"✅ 4 Kaynaktan ürünler başarıyla birleştirildi! (Toplam Ürün: {len(products_list)})")
    print(f"✅ Fotoğraflar ayrı tabloya (db_product_images.csv) yazıldı!")
    print("\n✨ 2. Katman Başarıyla Tamamlandı!")

if __name__ == "__main__":
    generate_catalog_layer()