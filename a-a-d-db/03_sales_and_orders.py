import pandas as pd
import uuid
import random

def generate_transactions_layer():
    print("🚀 3. Katman: Sipariş, Kargo ve Yorum Entegrasyonu Başladı...")

    try:
        # 1. ve 2. Katmanda ürettiğimiz dosyaları okuyoruz
        df_users = pd.read_csv('db_users.csv')
        df_products = pd.read_csv('db_products.csv')
        
        # Temizlenmiş işlem verilerini okuyoruz (EKSİK OLANLAR EKLENDİ)
        df_retail = pd.read_csv('cleaned_online_retail.csv')
        df_amazon = pd.read_csv('cleaned_amazon_sales.csv') 
        df_pakistan = pd.read_csv('cleaned_pakistan_ecommerce.csv', low_memory=False) 
        df_shipping = pd.read_csv('cleaned_shipping_data.csv')
        df_reviews = pd.read_csv('cleaned_amazon_reviews.csv', sep='\t')
    except FileNotFoundError as e:
        print(f"❌ Hata: Dosyalar bulunamadı! {e}")
        return

    # Hızlı arama (O(1) lookup) için Sözlükler
    valid_users = set(df_users['user_id'].tolist())
    # Sadece CUSTOMER rolündeki kullanıcıları bir listeye alıyoruz (Amazon/Pakistan atamaları için)
    customer_users = df_users[df_users['role'] == 'CUSTOMER']['user_id'].tolist()
    
    sku_to_prod_id = dict(zip(df_products['sku'], df_products['product_id']))
    prod_to_store_id = dict(zip(df_products['product_id'], df_products['store_id']))

    orders_list = []
    order_items_list = []
    shipments_list = []
    addresses_list = []
    reviews_list = []
    
    # Adres çakışmasını önlemek ve aynı adresi tekrar kullanmak için Sözlük (Dict) yapısı
    user_addresses_map = {}

    def get_or_create_address(u_id, default_city, default_country):
        """Kullanıcının adresi varsa döndürür, yoksa yeni oluşturur."""
        if u_id not in user_addresses_map:
            addr_id = f"ADDR_{uuid.uuid4().hex[:8].upper()}"
            addresses_list.append({
                'address_id': addr_id,
                'user_id': u_id,
                'address_title': 'Home',
                'address_line': f"{random.randint(1, 999)} E-Commerce Blvd",
                'city': str(default_city),
                'state': 'N/A',
                'country': str(default_country),
                'postal_code': f"{random.randint(10000, 99999)}",
                'is_default': True,
                'is_active': True
            })
            user_addresses_map[u_id] = addr_id
        return user_addresses_map[u_id]

    print("⏳ Siparişler (Orders & Order Items) oluşturuluyor...")
    
    # ==========================================
    # A. ONLINE RETAIL SİPARİŞLERİ
    # ==========================================
    grouped_retail = df_retail.groupby('InvoiceNo')
    limit_retail = 3000 # Dengeli dağılım için limitleri böldük
    count_retail = 0
    
    for invoice_no, group in grouped_retail:
        if count_retail >= limit_retail: break
        
        user_id = f"RET_{int(group['CustomerID'].iloc[0])}"
        if user_id not in valid_users: continue
            
        address_id = get_or_create_address(user_id, group['Country'].iloc[0], group['Country'].iloc[0])
        order_id = f"ORD_{uuid.uuid4().hex[:8].upper()}"
        order_date = group['InvoiceDate'].iloc[0]
        
        total_amount = 0.0
        valid_items = []
        primary_store_id = None
        
        for _, item_row in group.iterrows():
            sku = f"RET_{item_row['StockCode']}"
            product_id = sku_to_prod_id.get(sku)
            
            if product_id:
                qty = int(item_row['Quantity'])
                price = float(item_row['UnitPrice'])
                total_amount += (qty * price)
                
                # Doğru Mağaza Ataması (Hardcoded yerine dinamik)
                item_store_id = prod_to_store_id.get(product_id)
                if not primary_store_id: primary_store_id = item_store_id
                
                valid_items.append({
                    'order_item_id': f"ITEM_{uuid.uuid4().hex[:8].upper()}",
                    'order_id': order_id,
                    'product_id': product_id,
                    'quantity': qty,
                    'unit_price_at_sale': price,
                    'total_item_price': round(qty * price, 2)
                })
        
        # Siparişlerin yaklaşık %20'sine rastgele bir kupon atayalım
        applied_coupon = random.choices([None, 'CPN_1', 'CPN_2', 'CPN_4'], weights=[80, 10, 5, 5])[0]

        if valid_items and primary_store_id:
            orders_list.append({
                'order_id': order_id,
                'user_id': user_id, 
                'store_id': primary_store_id,
                'coupon_id': applied_coupon,
                'order_number': str(invoice_no),
                'total_amount': round(total_amount, 2),
                'status': random.choices(['Pending', 'Completed', 'Shipped'], weights=[10, 70, 20])[0],
                'fulfilment': 'Merchant',
                'order_date': order_date
            })
            order_items_list.extend(valid_items)
            
            # Kargo
            ship_row = df_shipping.sample(1).iloc[0]
            shipments_list.append({
                'shipment_id': f"SHIP_{uuid.uuid4().hex[:8].upper()}",
                'order_id': order_id, 'address_id': address_id,
                'tracking_number': f"TRK{random.randint(10000000, 99999999)}",
                'carrier_name': 'Global Logistics',
                'warehouse_block': ship_row['Warehouse_block'],
                'mode_of_shipment': ship_row['Mode_of_Shipment'],
                'shipping_status': 'Delivered' if ship_row['Reached_on_Time_Y_N'] == 1 else 'Delayed',
                'estimated_delivery': order_date
            })
            count_retail += 1

    # ==========================================
    # B. AMAZON SALES SİPARİŞLERİ 
    # ==========================================
    grouped_amazon = df_amazon.groupby('Order ID')
    limit_amazon = 3000
    count_amazon = 0
    
    for invoice_no, group in grouped_amazon:
        if count_amazon >= limit_amazon: break
        
        # Amazon verisinde user yoktu, rastgele müşteri atıyoruz
        user_id = random.choice(customer_users)
        address_id = get_or_create_address(user_id, "Amazon City", "USA")
        order_id = f"ORD_{uuid.uuid4().hex[:8].upper()}"
        order_date = group['Date'].iloc[0]
        
        total_amount = 0.0
        valid_items = []
        primary_store_id = None
        
        for _, item_row in group.iterrows():
            sku = f"AMZ_{item_row['SKU']}"
            product_id = sku_to_prod_id.get(sku)
            
            if product_id:
                qty = float(item_row['Qty']) if pd.notnull(item_row['Qty']) else 1.0
                price = float(item_row['Amount']) / qty if qty > 0 and pd.notnull(item_row['Amount']) else 0.0
                total_amount += (qty * price)
                
                item_store_id = prod_to_store_id.get(product_id)
                if not primary_store_id: primary_store_id = item_store_id
                
                valid_items.append({
                    'order_item_id': f"ITEM_{uuid.uuid4().hex[:8].upper()}",
                    'order_id': order_id, 'product_id': product_id,
                    'quantity': int(qty), 'unit_price_at_sale': round(price, 2),
                    'total_item_price': round(qty * price, 2)
                })
        
        # Amazon siparişleri için de kupon
        applied_coupon = random.choices([None, 'CPN_1', 'CPN_2', 'CPN_4'], weights=[80, 10, 5, 5])[0]

        if valid_items and primary_store_id:
            orders_list.append({
                'order_id': order_id, 'user_id': user_id, 'store_id': primary_store_id,
                'coupon_id': applied_coupon,
                'order_number': str(invoice_no), 'total_amount': round(total_amount, 2),
                'status': group['Status'].iloc[0] if pd.notnull(group['Status'].iloc[0]) else 'Completed',
                'fulfilment': group.get('Fulfilment', 'Amazon').iloc[0],
                'order_date': order_date
            })
            order_items_list.extend(valid_items)

            # Kargo 
            ship_row = df_shipping.sample(1).iloc[0]
            shipments_list.append({
                'shipment_id': f"SHIP_{uuid.uuid4().hex[:8].upper()}",
                'order_id': order_id, 'address_id': address_id,
                'tracking_number': f"TRK{random.randint(10000000, 99999999)}",
                'carrier_name': 'Global Logistics',
                'warehouse_block': ship_row['Warehouse_block'],
                'mode_of_shipment': ship_row['Mode_of_Shipment'],
                'shipping_status': 'Delivered' if ship_row['Reached_on_Time_Y_N'] == 1 else 'Delayed',
                'estimated_delivery': order_date
            })
            
            count_amazon += 1

    # ==========================================
    # C. PAKISTAN E-COMMERCE SİPARİŞLERİ 
    # ==========================================
    grouped_pakistan = df_pakistan.groupby('increment_id')
    limit_pakistan = 3000
    count_pakistan = 0
    
    for invoice_no, group in grouped_pakistan:
        if count_pakistan >= limit_pakistan: break
        
        user_id = random.choice(customer_users)
        address_id = get_or_create_address(user_id, "Lahore", "Pakistan")
        order_id = f"ORD_{uuid.uuid4().hex[:8].upper()}"
        order_date = group['created_at'].iloc[0]
        
        total_amount = 0.0
        valid_items = []
        primary_store_id = None
        
        for _, item_row in group.iterrows():
            if pd.isnull(item_row['sku']): continue
            sku = f"PAK_{item_row['sku']}"
            product_id = sku_to_prod_id.get(sku)
            
            if product_id:
                qty = float(item_row.get('qty_ordered', 1.0))
                price = float(item_row.get('price', 0.0))
                total_amount += (qty * price)
                
                item_store_id = prod_to_store_id.get(product_id)
                if not primary_store_id: primary_store_id = item_store_id
                
                valid_items.append({
                    'order_item_id': f"ITEM_{uuid.uuid4().hex[:8].upper()}",
                    'order_id': order_id, 'product_id': product_id,
                    'quantity': int(qty), 'unit_price_at_sale': round(price, 2),
                    'total_item_price': round(qty * price, 2)
                })
        
        # Pakistan siparişleri için de kupon
        applied_coupon = random.choices([None, 'CPN_1', 'CPN_2', 'CPN_4'], weights=[80, 10, 5, 5])[0]

        if valid_items and primary_store_id:
            orders_list.append({
                'order_id': order_id, 'user_id': user_id, 'store_id': primary_store_id,
                'coupon_id': applied_coupon,
                'order_number': str(invoice_no), 'total_amount': round(total_amount, 2),
                'status': str(group['status'].iloc[0]).capitalize() if pd.notnull(group['status'].iloc[0]) else 'Completed',
                'fulfilment': 'Merchant', 'order_date': order_date
            })
            order_items_list.extend(valid_items)

            # Kargo 
            ship_row = df_shipping.sample(1).iloc[0]
            shipments_list.append({
                'shipment_id': f"SHIP_{uuid.uuid4().hex[:8].upper()}",
                'order_id': order_id, 'address_id': address_id,
                'tracking_number': f"TRK{random.randint(10000000, 99999999)}",
                'carrier_name': 'Global Logistics',
                'warehouse_block': ship_row['Warehouse_block'],
                'mode_of_shipment': ship_row['Mode_of_Shipment'],
                'shipping_status': 'Delivered' if ship_row['Reached_on_Time_Y_N'] == 1 else 'Delayed',
                'estimated_delivery': order_date
            })
            
            count_pakistan += 1

    # ==========================================
    # D. YORUMLAR (Amazon Reviews)
    # ==========================================
    print("⏳ Yorumlar (Reviews & Sentiment) oluşturuluyor...")
    
    for _, row in df_reviews.dropna(subset=['product_id', 'customer_id']).iterrows():
        user_id = f"AMZ_{row['customer_id']}"
        sku = f"REV_{row['product_id']}"
        product_id = sku_to_prod_id.get(sku) 
        
        if user_id in valid_users and product_id:
            star = int(row['star_rating'])
            if star >= 4: sentiment = 'Positive'
            elif star == 3: sentiment = 'Neutral'
            else: sentiment = 'Negative'
            
            reviews_list.append({
                'review_id': row['review_id'],
                'user_id': user_id, 
                'product_id': product_id,
                'star_rating': star,
                'sentiment': sentiment,
                'comment': str(row['review_body'])[:250],
                'helpful_votes': int(row.get('helpful_votes', 0)),
                'is_verified_purchase': True if str(row.get('verified_purchase')) == 'Y' else False,
                'created_at': row['review_date']
            })

    # ==========================================
    # CSV KAYDETME
    # ==========================================
    pd.DataFrame(addresses_list).to_csv('db_user_addresses.csv', index=False)
    pd.DataFrame(orders_list).to_csv('db_orders.csv', index=False)
    pd.DataFrame(order_items_list).to_csv('db_order_items.csv', index=False)
    pd.DataFrame(shipments_list).to_csv('db_shipments.csv', index=False)
    pd.DataFrame(reviews_list).to_csv('db_reviews.csv', index=False)

    print(f"✅ Adresler oluşturuldu: {len(addresses_list)}")
    print(f"✅ Siparişler oluşturuldu: {len(orders_list)}")
    print(f"✅ Sipariş Detayları oluşturuldu: {len(order_items_list)}")
    print(f"✅ Kargolar oluşturuldu: {len(shipments_list)}")
    print(f"✅ Yorumlar eklendi: {len(reviews_list)}")
    print("\n🎉 3. KATMAN VE TÜM VERİ MÜHENDİSLİĞİ SÜRECİ KUSURSUZCA BİTTİ!")

if __name__ == "__main__":
    generate_transactions_layer() 