# E-Commerce Analytics Microservice

Spring Boot ile yazılmış profesyonel **E-Commerce Platform** Analytics Microservice.

## Özellikler ✨

- ✅ JWT Token tabanlı kimlik doğrulama
- ✅ Role-based access control (ADMIN, INDIVIDUAL, CORPORATE)
- ✅ REST API with Swagger/OpenAPI dokümantasyonu
- ✅ WebSocket gerçek zamanlı bildirimler
- ✅ Spring Data JPA ORM
- ✅ MySQL veritabanı desteği
- ✅ CORS yapılandırması (Angular/React için)
- ✅ Global exception handling
- ✅ Modular yapı (Controller → Service → Repository)

## Gereksinimler

- Java 17+
- Maven 3.6+
- MySQL 8.0+

## Kurulum

### 1. Projeyi Klonla ve Bağımlılıkları Yükle

```bash
cd /Users/fatmanurtoklu/Desktop/a-a-d-sb
mvn clean install
```

### 2. Veritabanını Oluştur

```sql
CREATE DATABASE ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. application.properties Dosyasını Yapılandır

`src/main/resources/application.properties` dosyasını düzenleyin:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# JWT Secret (MIN 256 BITS)
app.jwtSecret=your-super-secret-key-minimum-256-bits-long-please-change-this-value
app.jwtExpirationMs=86400000
```

### 4. Uygulamayı Çalıştır

```bash
mvn spring-boot:run
```

Veya VS Code'da `Spring Boot: Run` task'ını çalıştırın.

## API Dokümantasyonu

Uygulama çalıştıktan sonra:

- **Swagger UI**: [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/api/v3/api-docs](http://localhost:8080/api/v3/api-docs)

## Proje Yapısı

Detaylı proje yapısı için bkz: [E-COMMERCE-ARCHITECTURE.md](E-COMMERCE-ARCHITECTURE.md)

```
src/main/java/com/ecommerce/analytics/
├── config/          # Security, Swagger, WebSocket, CORS
├── controller/      # REST API endpoints
├── service/         # Business logic
├── repository/      # Database queries
├── model/           # Entity classes
├── dto/             # Request/Response objects
├── security/        # JWT & Authentication
└── exception/       # Global error handling
```

## Ana API Endpoint'leri

### 🔐 Kimlik Doğrulama
```
POST   /api/auth/login      - Giriş
POST   /api/auth/register   - Kayıt
POST   /api/auth/refresh    - Token Yenile
```

### 🛍️ Ürünler (Herkese Açık)
```
GET    /api/products        - Tüm ürünler
GET    /api/products/{id}   - Ürün detayı
GET    /api/categories      - Kategoriler
GET    /api/search          - Ürün ara
```

### 👤 Kullanıcı Profili
```
GET    /api/users/profile   - Profilimi getir
PUT    /api/users/profile   - Profili güncelle
GET    /api/users/orders    - Siparişlerim
```

### 🛒 Sepet
```
GET    /api/users/cart                  - Sepeti görüntüle
POST   /api/users/cart/add              - Sepete ekle
DELETE /api/users/cart/remove/{itemId}  - Sepetten çıkar
```

### 🏢 Kurumsal (Corporate)
```
GET    /api/corporate/store             - Mağaza bilgisi
GET    /api/corporate/products          - Kurumsal ürünler
POST   /api/corporate/products          - Ürün ekle
PUT    /api/corporate/products/{id}     - Ürün güncelle
```

### 👨‍💼 Admin Paneli
```
GET    /api/admin/users                 - Tüm kullanıcılar
GET    /api/admin/orders                - Tüm siparişler
GET    /api/admin/statistics            - İstatistikler
DELETE /api/admin/users/{id}            - Kullanıcı sil
```

### 💬 Chatbot
```
POST   /api/chat/ask        - Soru sor
GET    /api/chat/history    - Sohbet geçmişi
```

## Kullanıcı Rolleri

| Rol | Erişim |
|-----|--------|
| ADMIN | Tüm özellikler, kullanıcı/sipariş yönetimi |
| INDIVIDUAL | Ürün görüntüleme, sipariş verme, profil |
| CORPORATE | Kendi ürünlerini yönetme, toplu satış |
| GUEST | Sadece ürün görüntüleme |

## JWT Token Kullanımı

### Token Alma

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Yanıt:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

### Token ile İstek Yapma

```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer {token}"
```

## Veritabanı Modeli

### User (Kullanıcılar)
- `id` - Birincil anahtar
- `username` - Kullanıcı adı (benzersiz)
- `email` - E-posta (benzersiz)
- `password` - Şifre (hash'lenmiş)
- `firstName`, `lastName` - İsim
- `role` - Kullanıcı rolü (ADMIN, INDIVIDUAL, CORPORATE, GUEST)
- `active` - Aktif/Pasif durumu

### Product (Ürünler)
- `id` - Birincil anahtar
- `name` - Ürün adı
- `description` - Açıklama
- `price` - Fiyat
- `stock` - Stok miktarı
- `category` - Kategori
- `active` - Aktif durumu

### Order (Siparişler)
- `id` - Birincil anahtar
- `userId` - Sipariş veren kullanıcı
- `totalAmount` - Toplam tutar
- `status` - Sipariş durumu (PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED)
- `createdAt` - Oluşturulma tarihi
- `updatedAt` - Güncellenme tarihi

### ChatSession (Sohbet Oturumları)
- `id` - Birincil anahtar
- `userId` - Kullanıcı ID
- `messages` - Mesajlar (LONGTEXT)
- `createdAt` - Oluşturulma tarihi
- `updatedAt` - Güncellenme tarihi

## Ek Konfigürasyonlar

### CORS Ayarları (WebConfig.java)

İzin verilen kaynaklar:
- `http://localhost:4200` (Angular)
- `http://localhost:3000` (React)

### WebSocket Endpoint

- Bağlantı: `ws://localhost:8080/api/ws-notifications`
- Subscribe: `/topic/notifications`

### Swagger/OpenAPI

- UI: `/api/swagger-ui.html`
- Dokümantasyon: `/api/v3/api-docs`
- YAML formatı: `/api/v3/api-docs.yaml`

## İleri Geliştirmeler

1. **Database Optimizasyonu**
   - Index'ler ekle
   - Query performance tuning
   - Caching mekanizması (Redis)

2. **Testing**
   - Unit tests (JUnit 5)
   - Integration tests
   - API tests (Postman/REST Assured)

3. **Microservice Yapısı**
   - API Gateway
   - Service discovery
   - Config Server

4. **DevOps**
   - Docker containerization
   - Kubernetes deployment
   - CI/CD pipeline

5. **İzleme & Logging**
   - ELK Stack (Elasticsearch, Logstash, Kibana)
   - Prometheus metrics
   - Distributed tracing

## Hata Ayıklama

### Veri Tabanı Bağlantı Hatası
```
org.hibernate.HibernateException: Unable to create requested service [org.hibernate.engine.jdbc.env.spi.JdbcEnvironment]
```

**Çözüm:** MySQL'in çalıştığını ve bağlantı bilgilerinin doğru olduğunu kontrol edin.

### JWT Token Hatası
```
io.jsonwebtoken.SignatureException: JWT signature does not match
```

**Çözüm:** `app.jwtSecret` değerini min 256 bits olacak şekilde güncelleyin.

### CORS Hatası
```
Access to XMLHttpRequest at 'http://localhost:8080/api/...' from origin 'http://localhost:4200' has been blocked by CORS policy
```

**Çözüm:** `WebConfig.java`'da uygun origin ekleyin.

## Katkılar

Katkılarınız beklenmektedir! Bir PR göndermeden önce issue açıp tartışınız.

## Lisans

MIT License

---

**Yapı:** Spring Boot 3.2.0 | **Java:** 17+ | **Veritabanı:** MySQL 8.0+

📚 [E-COMMERCE-ARCHITECTURE.md](E-COMMERCE-ARCHITECTURE.md) dosyasında detaylı bilgiler bulabilirsiniz.
