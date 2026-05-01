# DataPulse — E-Commerce Analytics Platform

Full-stack e-ticaret analitik platformu. Rol tabanlı dashboard'lar, gerçek zamanlı bildirimler ve doğal dil ile veritabanı sorgulayabilen çok ajanlı bir AI chatbot içerir.

---

## Mimari

```
a-a-d-an/   →   Angular 21        (Frontend)
a-a-d-sb/   →   Spring Boot 3.2   (REST API Backend)
a-a-d-ai/   →   FastAPI + LangGraph  (AI Chatbot Servisi)
a-a-d-db/   →   PostgreSQL seed scriptleri
```

---

## Özellikler

### Kullanıcı Rolleri
- **Admin** — kullanıcı yönetimi, kategori yönetimi, gelir analitiği, denetim logları
- **Kurumsal (Mağaza Sahibi)** — sipariş takibi, stok yönetimi, müşteri analitiği, kargo
- **Bireysel (Müşteri)** — ürün listeleme, sepet & checkout, sipariş geçmişi, profil

### AI Chatbot (DataPulse AI)
- Doğal dil sorusunu otomatik olarak SQL sorgusuna dönüştürür (Text2SQL)
- LangGraph ile çok ajanlı akış: guardrail → SQL üretimi → hata düzeltme → analiz → görselleştirme
- Llama 3.3 70B modeli (Groq API üzerinden)
- Role göre veri erişim kısıtlaması (admin her şeyi, mağaza sahibi kendi verisini görür)
- Plotly ile otomatik grafik üretimi

### Teknik
- JWT tabanlı kimlik doğrulama (access + refresh token)
- WebSocket ile gerçek zamanlı bildirimler
- Swagger UI (`/api/swagger-ui.html`)

---

## Teknoloji Yığını

| Katman | Teknoloji |
|---|---|
| Frontend | Angular 21, TypeScript |
| Backend | Spring Boot 3.2, Java 21, Spring Security, JPA |
| AI Servisi | FastAPI, LangGraph, LangChain, Groq (Llama 3.3 70B) |
| Veritabanı | PostgreSQL |
| Güvenlik | JWT, BCrypt |

---

## Kurulum

### Gereksinimler
- Java 21
- Node.js 18+
- Python 3.11+
- PostgreSQL 15+

---

### 1. Veritabanı

```sql
CREATE DATABASE ecommerce_db;
```

Ardından `a-a-d-db/` içindeki scriptleri sırayla çalıştırın:

```bash
cd a-a-d-db
python 01_identity_management.py
python 02_catalog_and_inventory.py
python 03_sales_and_orders.py
# ... diğer scriptler sırayla
```

---

### 2. AI Servisi (Port: 8000)

```bash
cd a-a-d-ai
cp .env.example .env
# .env dosyasına kendi GROQ_API_KEY ve DB bilgilerinizi girin

pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

`.env` içeriği:
```
GROQ_API_KEY=your_groq_api_key_here
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ecommerce_db
DB_USER=postgres
DB_PASSWORD=your_password
```

> Groq API key almak için: [console.groq.com](https://console.groq.com)

---

### 3. Spring Boot Backend (Port: 8080)

`a-a-d-sb/src/main/resources/application.properties` dosyasındaki DB bilgilerini güncelleyin:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

```bash
cd a-a-d-sb
./mvnw spring-boot:run
```

API dökümantasyonu: `http://localhost:8080/api/swagger-ui.html`

---

### 4. Angular Frontend (Port: 4200)

```bash
cd a-a-d-an
npm install
ng serve
```

Uygulama: `http://localhost:4200`

---

## Veritabanı Tabloları

`users`, `customer_profiles`, `user_addresses`, `stores`, `categories`, `products`, `product_images`, `inventory`, `orders`, `order_items`, `payments`, `shipments`, `carts`, `cart_items`, `reviews`, `coupons`

---

## Ekran Görüntüleri

<img width="768" height="416" alt="image" src="https://github.com/user-attachments/assets/5dfcc589-fa75-49f8-9354-d4afa08a5f65" />
<img width="1023" height="552" alt="image" src="https://github.com/user-attachments/assets/cf3f47b4-919d-45d2-b3b6-fc56689975f9" />
<img width="768" height="417" alt="image" src="https://github.com/user-attachments/assets/13b0de35-1f0d-4287-b9a9-931a804899d0" />
<img width="768" height="412" alt="image" src="https://github.com/user-attachments/assets/ff3be71c-c5ff-438e-9d3f-533504ef817d" />
<img width="768" height="413" alt="image" src="https://github.com/user-attachments/assets/70275743-244d-4b6c-81b5-46cfff1a5763" />
<img width="768" height="414" alt="image" src="https://github.com/user-attachments/assets/20edc535-d4d9-4963-84dd-873368cd7d81" />
<img width="768" height="414" alt="image" src="https://github.com/user-attachments/assets/b309eb08-f040-44d7-8414-2c4affba109f" />
<img width="768" height="414" alt="image" src="https://github.com/user-attachments/assets/dd7305e0-4704-4a26-bc51-cfdc4de4fe8d" />
<img width="1470" height="794" alt="Ekran Resmi 2026-05-01 17 55 58" src="https://github.com/user-attachments/assets/4da83c0c-bbc5-44b5-a6f9-898e52b7dbc1" />
<img width="1470" height="788" alt="Ekran Resmi 2026-05-01 17 56 13" src="https://github.com/user-attachments/assets/d2894c69-2f5f-4700-af18-e4b50d2d682d" />
<img width="1470" height="794" alt="Ekran Resmi 2026-05-01 17 57 46" src="https://github.com/user-attachments/assets/8d01443a-bddd-46b0-96fc-c312fa78a9f1" />










