# Spring Boot Java Geliştirme Ortamı

Bu proje, Spring Boot ile Java RESTful API uygulamaları geliştirmek için tam bir geliştirme ortamıdır.

## Proje Tamamlandı

✅ **Spring Boot projesinin kurulumu başarılı**

### Proje Yapısı

- **pom.xml** - Maven proje yapılandırması (Spring Boot 3.2.0, Java 17)
- **Application.java** - Ana uygulama sınıfı
- **HelloController.java** - Örnek REST API controller'ı
- **application.properties** - Uygulama ayarları

### Uygulamayı Çalıştırma

1. Terminalde VS Code task'ını kullanın: **Spring Boot: Run**
2. Veya terminal'de çalıştırın:
   ```bash
   mvn spring-boot:run
   ```

### API Endpoint'leri

Uygulama `http://localhost:8080/api` adresinde çalışmaktadır:

- **Ana sayfa**: GET `http://localhost:8080/api/`
- **Merhaba**: GET `http://localhost:8080/api/hello`
- **Parametre ile**: GET `http://localhost:8080/api/hello?name=YadinizAdiniz`

### Yeni Controller'lar Ekleme

`src/main/java/com/example/demo/controller/` klasörüne yeni REST controller'ları ekleyebilirsiniz.

Örnek controller yapısı:

```java
@RestController
@RequestMapping("/api/resource")
public class ResourceController {
    
    @GetMapping
    public List<Resource> getAll() {
        // Implementasyon
    }
    
    @PostMapping
    public Resource create(@RequestBody Resource resource) {
        // Implementasyon
    }
}
```

### Service Katmanı Ekleme

`src/main/java/com/example/demo/service/` klasörüne yeni service'ler ekleyebilirsiniz:

```java
@Service
public class ResourceService {
    
    public List<Resource> getAllResources() {
        // İş mantığı
    }
}
```

### Bağımlılık Ekleme

Yeni bağımlılıklar eklemek için `pom.xml` dosyasını düzenleyin:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

Sonra `mvn clean install` komutunu çalıştırın.

### Geliştirme İpuçları

- **Hot Reload**: Spring DevTools sayesinde dosyaları kaydederken otomatik olarak yeniden yüklenir
- **Logging**: `application.properties` dosyasında `logging.level` ayarlarını düzenleyin
- **Portları Değiştirme**: `application.properties` dosyasında `server.port` değerini değiştirin

### İleri Düzey Konular

- Veritabanı entegrasyonu (MySQL, PostgreSQL vs.)
- JPA/Hibernate ORM kullanımı
- Spring Security ile güvenlik
- Swagger/OpenAPI dokümantasyonu
- Docker ile container'laştırma

## Kaynaklar

- [Spring Boot Resmi Dokümentasyonu](https://spring.io/projects/spring-boot)
- [Spring Framework Rehberi](https://spring.io/guides)
- [Maven Dokümentasyonu](https://maven.apache.org/guides/)
