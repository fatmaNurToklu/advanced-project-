import pandas as pd
import uuid
import random
from datetime import datetime, timedelta

def generate_final_ecosystem():
    print("🚀 4. Katman: Ödemeler, Stok, Sepet ve Kuponlar Hazırlanıyor...")

    try:
        df_orders = pd.read_csv('db_orders.csv')
        df_users = pd.read_csv('db_users.csv')
        df_products = pd.read_csv('db_products.csv')
    except FileNotFoundError as e:
        print(f"❌ Hata: Dosyalar bulunamadı! Lütfen önceki katmanları çalıştırdığından emin ol. \nDetay: {e}")
        return

    now = datetime.now()
    
    payments_list = []
    inventory_list = []
    carts_list = []
    cart_items_list = []
    coupons_list = []

    # ==========================================
    # 1. PAYMENTS (ÖDEMELER) 
    # ==========================================
    print("💸 Ödeme kayıtları (Payments) oluşturuluyor...")
    payment_methods = ['Credit_Card', 'Wire_Transfer', 'Cash_on_Delivery']
    
    for row in df_orders.itertuples(): # iterrows yerine daha hızlı olan itertuples kullanıldı
        status = 'Success' if row.status in ['Completed', 'Shipped'] else 'Pending'
        
        payments_list.append({
            'payment_id': f"PAY_{uuid.uuid4().hex[:8].upper()}",
            'order_id': row.order_id,
            'payment_method': random.choices(payment_methods, weights=[80, 10, 10])[0], 
            'transaction_id': f"TXN-{random.randint(100000000, 999999999)}" if status == 'Success' else None,
            'payment_status': status,
            'paid_at': row.order_date if status == 'Success' else None
        })

    # ==========================================
    # 2. INVENTORY (STOK VE ENVANTER)
    # ==========================================
    print("📦 Stok verileri (Inventory) hesaplanıyor (Hızlandırılmış Döngü)...")
    
    # 182.000 ürün için iterrows yerine itertuples ile ışık hızında dönüyoruz
    for row in df_products.itertuples():
        qty = random.randint(0, 500)
        
        if qty == 0: inv_status = 'OUT_OF_STOCK'
        elif qty < 20: inv_status = 'REPLENISHING' 
        else: inv_status = 'IN_STOCK'
            
        inventory_list.append({
            'inventory_id': f"INV_{uuid.uuid4().hex[:8].upper()}",
            'product_id': row.product_id,
            'quantity': qty,
            'low_stock_threshold': 20,
            'bin_location': f"{random.choice('ABCDEF')}-{random.randint(1, 99)}", 
            'status': inv_status,
            'last_stock_update': now.strftime('%Y-%m-%d %H:%M:%S')
        })

    # ==========================================
    # 3. CARTS & CART ITEMS (AKTİF/TERK EDİLMİŞ SEPETLER)
    # ==========================================
    print("🛒 Aktif alışveriş sepetleri (Carts) oluşturuluyor...")
    
    # ÇÖKME ÖNLEYİCİ: Kullanıcı sayısı 500'den azsa olanı al, çoksa 500 al.
    num_samples = min(500, len(df_users)) 
    sample_users = df_users.sample(num_samples)
    sample_products = df_products['product_id'].tolist()
    
    for row in sample_users.itertuples():
        cart_id = f"CART_{uuid.uuid4().hex[:8].upper()}"
        carts_list.append({
            'cart_id': cart_id,
            'user_id': row.user_id,
            'created_at': (now - timedelta(days=random.randint(1, 5))).strftime('%Y-%m-%d %H:%M:%S'),
            'updated_at': now.strftime('%Y-%m-%d %H:%M:%S')
        })
        
        num_items = random.randint(1, 4)
        cart_prods = random.sample(sample_products, num_items)
        
        for cp in cart_prods:
            cart_items_list.append({
                'cart_item_id': f"C_ITEM_{uuid.uuid4().hex[:8].upper()}",
                'cart_id': cart_id,
                'product_id': cp,
                'quantity': random.randint(1, 3)
            })

    # ==========================================
    # 4. COUPONS (PAZARLAMA KAMPANYALARI)
    # ==========================================
    print("🎟️ İndirim kuponları (Coupons) tanımlanıyor...")
    coupons_list = [
        {'coupon_id': 'CPN_1', 'code': 'WELCOME2026', 'discount_percentage': 15.0, 'expiry_date': '2026-12-31 23:59:59', 'is_active': True},
        {'coupon_id': 'CPN_2', 'code': 'SUMMER10', 'discount_percentage': 10.0, 'expiry_date': '2026-08-31 23:59:59', 'is_active': True},
        {'coupon_id': 'CPN_3', 'code': 'DATAPULSE50', 'discount_percentage': 50.0, 'expiry_date': '2026-05-01 00:00:00', 'is_active': False},
        {'coupon_id': 'CPN_4', 'code': 'TECH25', 'discount_percentage': 25.0, 'expiry_date': '2026-11-30 23:59:59', 'is_active': True}
    ]

    # ==========================================
    # CSV KAYDETME
    # ==========================================
    pd.DataFrame(payments_list).to_csv('db_payments.csv', index=False)
    pd.DataFrame(inventory_list).to_csv('db_inventory.csv', index=False)
    pd.DataFrame(carts_list).to_csv('db_carts.csv', index=False)
    pd.DataFrame(cart_items_list).to_csv('db_cart_items.csv', index=False)
    pd.DataFrame(coupons_list).to_csv('db_coupons.csv', index=False)

    print(f"\n✅ Ödemeler eklendi: {len(payments_list)}")
    print(f"✅ Stok kayıtları (Inventory) eklendi: {len(inventory_list)}")
    print(f"✅ Sepetler (Carts) eklendi: {len(carts_list)}")
    print(f"✅ Sepet Ürünleri eklendi: {len(cart_items_list)}")
    print(f"✅ Kuponlar tanımlandı: {len(coupons_list)}")
    print("\n🎉 BÜYÜK BAŞARI! 15 TABLONUN TAMAMI EKSİKSİZ VE HATASIZ ÜRETİLDİ!")

if __name__ == "__main__":
    generate_final_ecosystem()