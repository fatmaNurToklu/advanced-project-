import pandas as pd
import uuid
import random
from datetime import datetime, timedelta
import hashlib

def generate_identity_layer():
    print("🚀 1. Katman: Dinamik Kullanıcı Eşleştirme Başladı...")

    try:
        df_cb = pd.read_csv('cleaned_customer_behavior.csv')
        df_retail = pd.read_csv('cleaned_online_retail.csv')
        # Not: Amazon Reviews çok büyük (3.5GB) olduğu için belleği korumak adına sadece gerekli sütunları okuyabilirsin.
        df_reviews = pd.read_csv('cleaned_amazon_reviews.csv', sep='\t', usecols=['customer_id'])
    except FileNotFoundError as e:
        print(f"❌ Hata: Dosyalar bulunamadı! Lütfen önce temizleme scriptini çalıştırın.\n{e}")
        return

    users_list = []
    profiles_list = []

    # ==========================================
    # 🎲 DİNAMİK VERİ HAVUZLARI VE YARDIMCI FONKSİYONLAR
    # ==========================================
    male_names = ["Liam", "Noah", "Oliver", "Elijah", "James", "William", "Benjamin", "Lucas", "Can", "Emre", "Ahmet", "Mehmet", "Ali"]
    female_names = ["Emma", "Olivia", "Ava", "Isabella", "Sophia", "Mia", "Charlotte", "Amelia", "Zeynep", "Elif", "Ayşe", "Fatma", "Deniz"]
    last_names = ["Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Yılmaz", "Kaya", "Demir", "Çelik", "Şahin", "Yıldız", "Öztürk"]
    cities = ["New York", "London", "Istanbul", "Los Angeles", "Berlin", "Dubai", "Paris", "Toronto", "Ankara", "Izmir", "Miami", "Tokyo"]
    memberships = ["Bronze", "Silver", "Gold", "Platinum"]
    
    roles = ["CUSTOMER", "CORPORATE", "ADMIN"]
    role_weights = [0.85, 0.12, 0.03] 

    def get_random_dates(start_year=2021):
        """Kayıt ve güncellenme tarihlerini mantıklı bir sırayla rastgele üretir."""
        start = datetime(start_year, 1, 1)
        end = datetime.now()
        created_at = start + timedelta(seconds=random.randint(0, int((end - start).total_seconds())))
        # Updated at, created_at'ten sonra olmalı
        updated_at = created_at + timedelta(seconds=random.randint(0, int((end - created_at).total_seconds())))
        return created_at.strftime('%Y-%m-%d %H:%M:%S'), updated_at.strftime('%Y-%m-%d %H:%M:%S')

    def get_user_identity(gender):
        """Cinsiyete uygun isim ve rastgele soyisim üretir."""
        if gender == 'Male':
            fn = random.choice(male_names)
        elif gender == 'Female':
            fn = random.choice(female_names)
        else:
            fn = random.choice(male_names + female_names)
        return fn, random.choice(last_names)

    def generate_fake_hash(email):
        """Her kullanıcı için benzersiz görünen veritabanı şifre formatı üretir."""
        salt = str(random.randint(1000, 9999))
        hashed = hashlib.sha256(email.encode()).hexdigest()[:30]
        return f"pbkdf2:sha256:600000${salt}${hashed}"

    # ==========================================
    # A1. CUSTOMER BEHAVIOR'DAN KULLANICILAR VE PROFİLLER
    # ==========================================
    for _, row in df_cb.iterrows():
        u_id = f"CB_{int(row['Customer ID'])}"
        gender = row['Gender']
        fn, ln = get_user_identity(gender)
        role = random.choices(roles, weights=role_weights, k=1)[0]
        email = f"{fn.lower()}.{ln.lower()}{random.randint(10,999)}@datapulse.com"
        created, updated = get_random_dates()
        
        users_list.append({
            'user_id': u_id,
            'first_name': fn, 'last_name': ln, 'email': email,
            'password_hash': generate_fake_hash(email),
            'phone': f"555-{random.randint(1000000, 9999999)}",
            'gender': gender, 'role': role, 'status': True,
            'created_at': created, 'updated_at': updated
        })

        # Profil oluştur (Sadece CUSTOMER rolü için)
        if role == 'CUSTOMER':
            profiles_list.append({
                'profile_id': f"PROF_{uuid.uuid4().hex[:10].upper()}",
                'user_id': u_id,
                'age': int(row['Age']),
                'city': row['City'],
                'membership_type': row['Membership Type'],
                'total_spend': float(row['Total Spend']),
                'satisfaction_level': row['Satisfaction Level'],
                'last_login': updated
            })

    # ==========================================
    # A2. ONLINE RETAIL'DAN KULLANICILAR VE PROFİLLER
    # ==========================================
    unique_retail_ids = df_retail['CustomerID'].unique()
    for rid in unique_retail_ids:
        u_id = f"RET_{int(rid)}"
        gender = random.choice(['Male', 'Female'])
        fn, ln = get_user_identity(gender)
        role = random.choices(roles, weights=role_weights, k=1)[0]
        email = f"{fn.lower()}.{ln.lower()}{random.randint(10,999)}@datapulse.com"
        created, updated = get_random_dates()
        
        users_list.append({
            'user_id': u_id,
            'first_name': fn, 'last_name': ln, 'email': email,
            'password_hash': generate_fake_hash(email),
            'phone': f"555-{random.randint(1000000, 9999999)}",
            'gender': gender, 'role': role, 'status': True,
            'created_at': created, 'updated_at': updated
        })

        # Eksik profilleri sentetik verilerle doldur
        if role == 'CUSTOMER':
            profiles_list.append({
                'profile_id': f"PROF_{uuid.uuid4().hex[:10].upper()}",
                'user_id': u_id,
                'age': random.randint(18, 65),
                'city': random.choice(cities),
                'membership_type': random.choices(memberships, weights=[0.5, 0.3, 0.15, 0.05])[0],
                'total_spend': 0.0, # Siparişler kısmında güncellenebilir
                'satisfaction_level': random.randint(1, 5),
                'last_login': updated
            })

    # ==========================================
    # A3. AMAZON REVIEWS'DAN KULLANICILAR VE PROFİLLER
    # ==========================================
    unique_amz_ids = df_reviews['customer_id'].unique()
    for aid in unique_amz_ids:
        u_id = f"AMZ_{aid}"
        gender = random.choice(['Male', 'Female'])
        fn, ln = get_user_identity(gender)
        role = random.choices(roles, weights=role_weights, k=1)[0]
        email = f"{fn.lower()}.{ln.lower()}{random.randint(10,999)}@datapulse.com"
        created, updated = get_random_dates()
        
        users_list.append({
            'user_id': u_id,
            'first_name': fn, 'last_name': ln, 'email': email,
            'password_hash': generate_fake_hash(email),
            'phone': f"555-{random.randint(1000000, 9999999)}",
            'gender': gender, 'role': role, 'status': True,
            'created_at': created, 'updated_at': updated
        })

        if role == 'CUSTOMER':
            profiles_list.append({
                'profile_id': f"PROF_{uuid.uuid4().hex[:10].upper()}",
                'user_id': u_id,
                'age': random.randint(18, 65),
                'city': random.choice(cities),
                'membership_type': random.choices(memberships, weights=[0.5, 0.3, 0.15, 0.05])[0],
                'total_spend': 0.0,
                'satisfaction_level': random.randint(1, 5),
                'last_login': updated
            })

    # ==========================================
    # A4. SABİT TEST HESAPLARI (Çok Önemli!)
    # ==========================================
    print("🔧 Giriş testleri için sabit Admin ve Corporate hesapları ekleniyor...")
    now_str = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    test_accounts = [
        {'id': 'ADMIN_999', 'role': 'ADMIN', 'email': 'admin@datapulse.com', 'first': 'Super', 'last': 'Admin'},
        {'id': 'CORP_998', 'role': 'CORPORATE', 'email': 'corp1@datapulse.com', 'first': 'Tech', 'last': 'Store'},
        {'id': 'CORP_997', 'role': 'CORPORATE', 'email': 'corp2@datapulse.com', 'first': 'Fashion', 'last': 'Hub'}
    ]
    
    for acc in test_accounts:
        users_list.append({
            'user_id': acc['id'],
            'first_name': acc['first'], 'last_name': acc['last'], 'email': acc['email'],
            'password_hash': generate_fake_hash(acc['email']),
            'phone': '555-0000000', 'gender': 'Other', 'role': acc['role'],
            'status': True, 'created_at': now_str, 'updated_at': now_str
        })

    # DataFramelere çevir ve kaydet
    df_final_users = pd.DataFrame(users_list)
    df_final_profiles = pd.DataFrame(profiles_list)

    df_final_users.to_csv('db_users.csv', index=False)
    df_final_profiles.to_csv('db_customer_profiles.csv', index=False)

    print(f"\n✨ 1. Katman Başarıyla Tamamlandı!")
    print(f"📊 Toplam Kullanıcı: {len(df_final_users)}")
    print(f"👤 Toplam Müşteri Profili: {len(df_final_profiles)}")

if __name__ == "__main__":
    generate_identity_layer()