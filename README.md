# DataPulse — E-Commerce Analytics Platform

![Angular](https://img.shields.io/badge/Angular-21-DD0031?style=flat&logo=angular&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=flat&logo=springboot&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-0.115-009688?style=flat&logo=fastapi&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?style=flat&logo=postgresql&logoColor=white)
![Python](https://img.shields.io/badge/Python-3.9-3776AB?style=flat&logo=python&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![LangGraph](https://img.shields.io/badge/LangGraph-0.2-1C3C3C?style=flat&logo=langchain&logoColor=white)

Full-stack e-ticaret analitik platformu. Rol tabanlı dashboard'lar, gerçek zamanlı bildirimler ve doğal dil ile veritabanı sorgulayabilen çok ajanlı bir AI chatbot içerir.

---

## İçindekiler

- [Ekran Görüntüleri](#ekran-görüntüleri)
- [Mimari](#mimari)
- [Özellikler](#özellikler)
- [Teknoloji Yığını](#teknoloji-yığını)
- [Kurulum](#kurulum)
  - [Veritabanı](#1-veritabanı)
  - [AI Servisi](#2-ai-servisi-port-8000)
  - [Spring Boot Backend](#3-spring-boot-backend-port-8080)
  - [Angular Frontend](#4-angular-frontend-port-4200)
- [Test Hesapları](#test-hesapları)
- [Veritabanı Tabloları](#veritabanı-tabloları)
- [Lisans](#lisans)
- [Geliştirici](#geliştirici)

---

## Ekran Görüntüleri

<img width="1470" height="796" alt="Ekran Resmi 2026-05-01 17 50 30" src="https://github.com/user-attachments/assets/4faec7be-fddf-4c0a-9507-1875995eff0b" />
<img width="1469" height="792" alt="Ekran Resmi 2026-05-01 17 50 59" src="https://github.com/user-attachments/assets/4848e883-ba5b-4be7-8081-34a0499cdf79" />
<img width="1470" height="797" alt="Ekran Resmi 2026-05-01 17 51 46" src="https://github.com/user-attachments/assets/0cdc6cf3-9230-45f9-92cc-f893882f96b0" />
<img width="1470" height="790" alt="Ekran Resmi 2026-05-01 17 53 21" src="https://github.com/user-attachments/assets/ca2d37e6-e078-4b76-aa66-fb69186da829" />
<img width="1470" height="791" alt="Ekran Resmi 2026-05-01 17 53 49" src="https://github.com/user-attachments/assets/f338914c-fb2d-4e6d-a640-f41deffde007" />
<img width="1470" height="792" alt="Ekran Resmi 2026-05-01 17 54 03" src="https://github.com/user-attachments/assets/29eddbc2-02dc-42fe-8f19-5de81e147eee" />
<img width="1470" height="793" alt="Ekran Resmi 2026-05-01 17 55 43" src="https://github.com/user-attachments/assets/96a74ee9-bc83-4a5e-807d-405315fcba97" />
<img width="1470" height="794" alt="Ekran Resmi 2026-05-01 17 55 58" src="https://github.com/user-attachments/assets/cdf795b9-b9c0-4978-b040-bd3feda299bd" />
<img width="1470" height="794" alt="Ekran Resmi 2026-05-01 17 55 58" src="https://github.com/user-attachments/assets/4da83c0c-bbc5-44b5-a6f9-898e52b7dbc1" />
<img width="1470" height="788" alt="Ekran Resmi 2026-05-01 17 56 13" src="https://github.com/user-attachments/assets/d2894c69-2f5f-4700-af18-e4b50d2d682d" />
<img width="1470" height="794" alt="Ekran Resmi 2026-05-01 17 57 46" src="https://github.com/user-attachments/assets/8d01443a-bddd-46b0-96fc-c312fa78a9f1" />

---

## Mimari

```
a-a-d-an/   →   Angular 21           (Frontend)
a-a-d-sb/   →   Spring Boot 3.2      (REST API Backend)
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
| Veritabanı | PostgreSQL 15 |
| Güvenlik | JWT, BCrypt |

---

## Kurulum

### Gereksinimler
- Java 21
- Node.js 18+
- Python 3.9+
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

python -m venv .venv
source .venv/bin/activate      # Windows: .venv\Scripts\activate
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

## Test Hesapları

Seed scriptleri çalıştırıldıktan sonra aşağıdaki hesaplarla giriş yapılabilir.  
**Tüm hesapların şifresi:** `Test1234`

| Rol | E-posta | Açıklama |
|---|---|---|
| Admin | `mia.kaya215@datapulse.com` | Tam yönetim erişimi |
| Kurumsal | `elif.smith766@datapulse.com` | Charlotte Store sahibi |
| Kurumsal | `ava.yılmaz668@datapulse.com` | Deniz Boutique sahibi |

---

## Veritabanı Tabloları

`users`, `customer_profiles`, `user_addresses`, `stores`, `categories`, `products`, `product_images`, `inventory`, `orders`, `order_items`, `payments`, `shipments`, `carts`, `cart_items`, `reviews`, `coupons`

---

## Lisans

Bu proje [MIT Lisansı](https://opensource.org/licenses/MIT) ile lisanslanmıştır.

---

## Geliştirici

**Fatma Nur Toklu**  
Bilgisayar Mühendisliği (İngilizce) — 2. Sınıf

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Fatma_Nur_Toklu-0077B5?style=flat&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/fatma-nur-toklu-669464340)
[![Email](https://img.shields.io/badge/Email-toklufatmanur07@gmail.com-D14836?style=flat&logo=gmail&logoColor=white)](mailto:toklufatmanur07@gmail.com)
