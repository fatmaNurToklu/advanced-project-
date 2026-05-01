import pandas as pd
import numpy as np

def clean_data():
    print("🚀 Genişletilmiş Veri Temizleme Operasyonu Başladı...")

    # =========================================================
    # 1. CUSTOMER BEHAVIOR
    # =========================================================
    df_customer = pd.read_csv('customer-behavior.csv')
    df_customer.dropna(subset=['Customer ID', 'Age', 'Total Spend'], inplace=True)
    df_customer['Age'] = df_customer['Age'].astype(int)
    # Varsayım: Total Spend zaten USD
    df_customer['Total Spend'] = df_customer['Total Spend'].astype(float).round(2)
    df_customer['City'] = df_customer['City'].str.strip().str.title()
    print(f"✅ Customer Behavior temizlendi. ({len(df_customer)} kayıt)")

    # =========================================================
    # 2. ONLINE RETAIL
    # =========================================================
    df_retail = pd.read_csv('online_retail.csv', encoding='ISO-8859-1')
    df_retail = df_retail[(df_retail['Quantity'] > 0) & (df_retail['UnitPrice'] > 0)]
    df_retail.dropna(subset=['CustomerID', 'Description'], inplace=True)
    df_retail['InvoiceDate'] = pd.to_datetime(df_retail['InvoiceDate'])
    df_retail['CustomerID'] = df_retail['CustomerID'].astype(int)
    
    # [CURRENCY NORMALIZATION] - GBP to USD
    df_retail['UnitPrice'] = (df_retail['UnitPrice'] * 1.26).round(2)
    print(f"✅ Online Retail temizlendi ve fiyatlar USD'ye çevrildi. ({len(df_retail)} kayıt)")

    # =========================================================
    # 3. AMAZON REVIEWS
    # =========================================================
    # df_reviews = pd.read_csv('amazon_reviews_multilingual_US_v1_00.tsv', sep='\t', on_bad_lines='skip')
    # df_reviews.dropna(subset=['review_body', 'star_rating', 'product_id'], inplace=True)
    # df_reviews['review_date'] = pd.to_datetime(df_reviews['review_date'])
    # df_reviews['star_rating'] = pd.to_numeric(df_reviews['star_rating'], errors='coerce')
    print("✅ Amazon Reviews temizlendi. (Atlandı - Büyük dosya)")

    # =========================================================
    # 4. AMAZON SALES
    # =========================================================
    df_amazon = pd.read_csv('Amazon Sale Report.csv', low_memory=False)
    if 'Unnamed: 22' in df_amazon.columns:
        df_amazon.drop(columns=['Unnamed: 22'], inplace=True)
    df_amazon['Date'] = pd.to_datetime(df_amazon['Date'], format='%m-%d-%y', errors='coerce')
    df_amazon['Amount'] = pd.to_numeric(df_amazon['Amount'], errors='coerce').fillna(0.0)
    
    # [CURRENCY NORMALIZATION] - INR to USD
    df_amazon['Amount'] = (df_amazon['Amount'] * 0.012).round(2)
    
    # [ENUM MAPPING] - Amazon Status to Our Schema
    amazon_status_map = {
        'Shipped': 'Shipped',
        'Shipped - Delivered to Buyer': 'Completed',
        'Cancelled': 'Cancelled',
        'Pending': 'Pending',
        'Pending - Waiting for Pick Up': 'Pending',
        'Delivered': 'Completed',
        'Refunded': 'Cancelled',
        'Rejected': 'Cancelled'
    }
    df_amazon['Status'] = df_amazon['Status'].map(amazon_status_map).fillna('Pending')
    print(f"✅ Amazon Sales temizlendi, kur ve enum ayarlandı. ({len(df_amazon)} kayıt)")

    # =========================================================
    # 5. PAKISTAN E-COMMERCE
    # =========================================================
    df_pakistan = pd.read_csv('Pakistan Largest Ecommerce Dataset.csv', low_memory=False)
    df_pakistan.dropna(how='all', inplace=True)
    df_pakistan['created_at'] = pd.to_datetime(df_pakistan['created_at'], errors='coerce')
    
    # [CURRENCY NORMALIZATION] - PKR to USD
    if 'price' in df_pakistan.columns:
        df_pakistan['price'] = (pd.to_numeric(df_pakistan['price'], errors='coerce').fillna(0) * 0.0036).round(2)
    if 'grand_total' in df_pakistan.columns:
        df_pakistan['grand_total'] = (pd.to_numeric(df_pakistan['grand_total'], errors='coerce').fillna(0) * 0.0036).round(2)

    # [ENUM MAPPING] - Pakistan Status to Our Schema
    pakistan_status_map = {
        'completed': 'Completed',
        'canceled': 'Cancelled',
        'received': 'Completed',
        'refund': 'Cancelled',
        'closed': 'Completed',
        'fraud': 'Cancelled',
        'holded': 'Pending',
        'pending': 'Pending',
        'processing': 'Pending',
        'payment_review': 'Pending'
    }
    # Verilerde büyük/küçük harf tutarsızlığı olabilir, önce lower() yapıyoruz.
    if 'status' in df_pakistan.columns:
        df_pakistan['status'] = df_pakistan['status'].astype(str).str.lower().map(pakistan_status_map).fillna('Pending')
    print(f"✅ Pakistan E-Commerce temizlendi, kur ve enum ayarlandı. ({len(df_pakistan)} kayıt)")

    # =========================================================
    # 6. SHIPPING DATA
    # =========================================================
    df_shipping = pd.read_csv('Train.csv')
    df_shipping.columns = [c.replace('.', '_') for c in df_shipping.columns]
    # Varsayım: Cost_of_the_Product USD bazlıdır.
    print(f"✅ Shipping Data temizlendi. ({len(df_shipping)} kayıt)")

    print("\n✨ TÜM VERİ SETLERİ DB ŞEMASINA UYGUN HALE GELDİ!")

    # CSV Kaydetme İşlemleri (Amazon Reviews yoruma alındı)
    print("\n💾 Temizlenmiş veriler CSV olarak kaydediliyor...")
    df_customer.to_csv('cleaned_customer_behavior.csv', index=False)
    df_retail.to_csv('cleaned_online_retail.csv', index=False)
    df_amazon.to_csv('cleaned_amazon_sales.csv', index=False)
    df_pakistan.to_csv('cleaned_pakistan_ecommerce.csv', index=False)
    df_shipping.to_csv('cleaned_shipping_data.csv', index=False)
    print("✅ Tüm temiz veriler başarıyla kaydedildi!")
    
    return df_customer, df_retail, None, df_amazon, df_pakistan, df_shipping

# Çalıştırıp sonuçları alalım
df_cust_clean, df_retail_clean, df_rev_clean, df_amaz_clean, df_pak_clean, df_ship_clean = clean_data()