# E-Commerce Analytics Microservice - Yapı Özeti

## Proje Başarıyla Oluşturuldu! ✅

Bu Spring Boot projesi **E-Commerce Analytics Platform** için profesyonel bir microservice yapısı sunmaktadır.

---

## 📁 Proje Yapısı

```
src/main/java/com/ecommerce/analytics/
│
├── AnalyticsApplication.java          # 🚀 Ana Uygulama Sınıfı
│
├── config/                            # ⚙️ Yapılandırma
│   ├── SecurityConfig.java            # Spring Security & JWT Kuralları
│   ├── SwaggerConfig.java             # OpenAPI/Swagger Dokümantasyonu
│   ├── WebConfig.java                 # CORS Ayarları (Angular/React için)
│   └── WebSocketConfig.java           # Gerçek zamanlı Bildirimler (WebSocket)
│
├── security/                          # 🔐 Güvenlik & Token
│   ├── JwtTokenProvider.java          # JWT Token Oluşturma & Doğrulama
│   ├── JwtAuthenticationFilter.java   # Token Kontrol Filtresi
│   └── CustomUserDetailsService.java  # Kullanıcı Yükleme Servis
│
├── exception/                         # ⚠️ Hata Yönetimi
│   ├── GlobalExceptionHandler.java    # Merkezi Hata Yakalayıcı
│   └── ResourceNotFoundException.java # Özel Hata Sınıfı
│
├── dto/                               # 📦 Data Transfer Objects
│   ├── request/
│   │   └── LoginRequest.java          # Login İsteği
│   └── response/
│       ├── AuthResponse.java          # Auth Yanıtı (Token)
│       └── UserProfileResponse.java   # Profil Yanıtı
│
├── model/                             # 🗄️ Veritabanı Entities
│   ├── User.java                      # Kullanıcı Entity
│   ├── Product.java                   # Ürün Entity
│   ├── Order.java                     # Sipariş Entity
│   ├── ChatSession.java               # Chat Oturumu Entity
│   └── enums/
│       ├── RoleType.java              # ADMIN, INDIVIDUAL, CORPORATE, GUEST
│       └── OrderStatus.java           # Sipariş Durumları
│
├── repository/                        # 🔍 Veritabanı Sorguları
│   ├── UserRepository.java            # Kullanıcı Sorgular
│   ├── ProductRepository.java         # Ürün Sorgular
│   ├── OrderRepository.java           # Sipariş Sorgular
│   └── ChatSessionRepository.java     # Chat Sorgular
│
├── service/                           # 💼 İş Mantığı
│   ├── AuthService.java               # Kimlik Doğrulama İşlemleri
│   ├── ProductService.java            # Ürün İşlemleri
│   ├── IndividualService.java         # Bireysel Kullanıcı İşlemleri
│   ├── CorporateService.java          # Kurumsal Kullanıcı İşlemleri
│   ├── AdminService.java              # Admin İşlemleri
│   └── ChatbotService.java            # Chatbot İşlemleri (Python entegrasyonu)
│
└── controller/                        # 🌐 REST API Endpoints
    ├── AuthController.java            # POST /api/auth/login, /register
    ├── PublicController.java          # GET /api/categories, /products
    ├── IndividualController.java      # GET /api/users/profile, /cart
    ├── CorporateController.java       # GET /api/corporate/store, /products
    ├── AdminController.java           # GET /api/admin/users, /orders
    └── ChatController.java            # POST /api/chat/ask
```

---

## 🚀 API Endpoint'leri

### 🔐 Authentication - `/api/auth`
- `POST /api/auth/login` - Kullanıcı giriş
- `POST /api/auth/register` - Yeni kullanıcı kayıt
- `POST /api/auth/refresh` - Token yenileme

### 🛍️ Public (Herkes) - `/api`
- `GET /api/categories` - Kategorileri getir
- `GET /api/products` - Tüm ürünleri getir
- `GET /api/products/{id}` - Ürün detayı
- `GET /api/search?query=...` - Ürün ara

### 👤 Individual (Bireysel Kullanıcı) - `/api/users`
- `GET /api/users/profile` - Profilimi getir
- `PUT /api/users/profile` - Profili güncelle
- `GET /api/users/cart` - Sepeti getir
- `POST /api/users/cart/add?productId=...` - Sepete ekle
- `GET /api/users/orders` - Siparişlerim

### 🏢 Corporate (Kurumsal) - `/api/corporate`
- `GET /api/corporate/store` - Mağaza bilgisi
- `GET /api/corporate/products` - Ürünlerim
- `POST /api/corporate/products` - Yeni ürün ekle
- `PUT /api/corporate/products/{id}` - Ürün güncelle

### 👨‍💼 Admin - `/api/admin`
- `GET /api/admin/users` - Tüm kullanıcılar
- `GET /api/admin/orders` - Tüm siparişler
- `GET /api/admin/statistics` - İstatistikler
- `DELETE /api/admin/users/{id}` - Kullanıcı sil

### 💬 Chatbot - `/api/chat`
- `POST /api/chat/ask?question=...` - Soru sor
- `GET /api/chat/history` - Sohbet geçmişi

---

## 📦 Teknolojiler

- **Spring Boot 3.2.0** - Framework
- **Java 17** - Programming Language
- **Spring Data JPA** - ORM (Hibernate)
- **Spring Security** - Güvenlik
- **JWT (JJWT)** - Token Kimlik Doğrulama
- **SpringDoc OpenAPI 2.5.0** - Swagger/OpenAPI Dokümantasyonu
- **WebSocket** - Gerçek zamanlı iletişim
- **MySQL** - Veritabanı (yapılandırılabilir)
- **Maven 3.9+** - Build Tool

---

## ⚙️ application.properties Ayarları

Uygulamayı `src/main/resources/application.properties` dosyasından yapılandırabilirsiniz:

```properties
# Server
server.port=8080
server.servlet.context-path=/api
spring.application.name=analytics-service

# Database (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT
app.jwtSecret=your-super-secret-key-min-256-bits-long-please-change-this-immediately
app.jwtExpirationMs=86400000

# Logging
logging.level.root=INFO
logging.level.com.ecommerce.analytics=DEBUG

# Swagger UI
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

---

## 🚀 Çalıştırma

### 1. Bağımlılıkları Yükle
```bash
mvn clean install
```

### 2. Veritabanını Oluştur
```sql
CREATE DATABASE ecommerce_db;
```

### 3. Uygulamayı Başlat
```bash
mvn spring-boot:run
```

Veya VS Code task kullanarak: **Spring Boot: Run**

### 4. API Dokümantasyonuna Erişim
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/v3/api-docs`

---

## 🔧 Yapılandırma Detayları

### 1. **Security Config** (`SecurityConfig.java`)
- CSRF koruması devre dışı (API için)
- Stateless session yönetimi (JWT)
- Public endpoint'ler: `/api/auth/**`, `/api/products`, `/api/categories`
- Diğer tüm endpoint'ler kimlik doğrulama gerektirir

### 2. **CORS Config** (`WebConfig.java`)
- Angular/React uygulamaları (`localhost:3000`, `localhost:4200`) erişim izni
- Tüm HTTP metodları ve headers izinli

### 3. **WebSocket** (`WebSocketConfig.java`)
- Gerçek zamanlı bildirimler için
- Endpoint: `/ws-notifications`
- Subscription topic: `/topic/notifications`

### 4. **JWT Security**
- Token geçerlilik süresi: 24 saat (yapılandırılabilir)
- Header format: `Authorization: Bearer {token}`
- Algorithm: HS512

---

## 📊 Kullanıcı Rolleri

1. **ADMIN** - Sistem Yöneticisi
   - Tüm verileri görebilir
   - Kullanıcıları yönetebilir
   - İstatistikleri görebilir

2. **INDIVIDUAL** - Bireysel Kullanıcı
   - Ürünleri görüntüleyebilir
   - Sipariş verebilir
   - Sepet kullanabilir

3. **CORPORATE** - Kurumsal Kullanıcı
   - Kendi ürünlerini yönetebilir
   - Toplu satış yapabilir

4. **GUEST** - Ziyaretçi
   - Sadece ürünleri görüntüleyebilir

---

## 🔗 Python Chatbot Entegrasyonu

`ChatbotService.java` Python AI servisine istek gönderir. Bağlantı kurmak için:

```python
# FastAPI örneği
@app.post("/api/ask")
async def ask_question(question: str):
    # AI modeli ile cevap üret
    return {"answer": "Cevap"}
```

---

## 📝 İngilizce Kaynak Yolları Talimatları

1. **Model Sınıflarını Genişlet**
   - `Address.java`, `Payment.java`, `Review.java` ekle

2. **Repository Sorgularını Zenginleştir**
   - `@Query` annotations kullanarak custom sorgular ekle
   - Pagination ve sorting ekle

3. **Servis Katmanını Tamamla**
   - Business logic kurallarını implement et
   - Exception handling ekle

4. **Controller'ları Güçlendir**
   - Validation (@Valid) ekle
   - Proper HTTP status codes dön
   - API versioning ekle (/api/v1/...)

5. **Testing Ekle**
   - Unit tests (@SpringBootTest)
   - Integration tests
   - Controller tests

6. **Database Optimizasyon**
   - Indexes ekle
   - Lazy/Eager loading ayarla
   - Caching mekanizması ekle

---

## 🐛 Hata Çözümü

### Lombok Sorunu
Pom.xml'den Lombok bağımlılığı kaldırıldı. Manuel getters/setters kullanılmaktadır.

### Jakarta Persistence
Spring Boot 3.2.0 Jakarta EE kullanır (javax yerine jakarta).

### WebSocket Bağımlılığı
Eğer WebSocket'i kullanmazsa, `WebSocketConfig.java` saf tutulabilir.

---

## 📚 Kaynaklar

- [Spring Boot Dokümantasyonu](https://spring.io/projects/spring-boot)
- [Spring Security OAuth2](https://spring.io/projects/spring-security)
- [JWT ile Kimlik Doğrulama](https://jwt.io)
- [SpringDoc OpenAPI](https://springdoc.org)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)

---

## 📞 İletişim & Destek

Sorularınız veya önerileriniz için GitHub Issues'u kullanabilirsiniz.

---

**Proje Durumu:** ✅ Hazır - İleri Geliştirme için Hazır  
**Son Güncelleme:** Nisan 2026
