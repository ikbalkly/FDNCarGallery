# FDN Car Gallery

Çok şubeli bir oto galeri zincirinin şube, personel, araç stoğu ve satış süreçlerini yöneten **Spring Boot REST API** projesi.

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue)
![Durum](https://img.shields.io/badge/durum-geliştiriliyor-yellow)

---

## 🚧 Proje Durumu

> **Bu proje aktif olarak geliştirilmektedir. Henüz tamamlanmış bir sürüm yoktur.**
>
> - Şu an **kimlik doğrulama, şube, personel (şube yöneticisi, müdür, satış temsilcisi), marka/model, araç stoğu, müşteri, araç alımı ve bakım** modülleri çalışır durumda; yeni personele geçici şifresi e-posta ile iletiliyor, ayrılan personel yeniden işe alınabiliyor.
> - Satış, rezervasyon ve ekspertiz modüllerinin **entity / DTO / mapper katmanları hazır**, servis ve controller katmanları yazılıyor.
> - API sözleşmeleri (endpoint isimleri, request/response alanları) geliştirme sürecinde **değişebilir**.
> - Ayrıntılı durum için aşağıdaki [Yol Haritası](#-yol-haritası) bölümüne bakabilirsiniz.

---

## İçindekiler

- [Proje Hakkında](#proje-hakkında)
- [Teknoloji Yığını](#teknoloji-yığını)
- [Mimari](#mimari)
- [Domain Modeli](#domain-modeli)
- [Güvenlik ve Yetkilendirme](#güvenlik-ve-yetkilendirme)
- [Kurulum](#kurulum)
- [API Uçları](#api-uçları)
- [Hata Formatı](#hata-formatı)
- [Yol Haritası](#-yol-haritası)

---

## Proje Hakkında

FDN Car Gallery, birden fazla şubesi olan bir oto galerinin günlük operasyonlarını tek bir sistem üzerinden yönetmeyi hedefler:

- **Şube yönetimi** — şube açma, adres ve müdür ataması, şube bazlı veri izolasyonu
- **Personel yönetimi** — şube yöneticisi, müdür ve satış temsilcisi kayıtları; her personele otomatik kurumsal kullanıcı hesabı
- **Stok yönetimi** — aracın galeriye girişi, şubeler arası transferi, satış durumunun takibi
- **Müşteri yönetimi** — bireysel (TCKN) ve kurumsal (VKN) müşteri kayıtları, tüm şubelerde ortak
- **Alım / satış süreçleri** — müşteriden araç alımı ve bakım kayıtları; satış, prim hesabı, rezervasyon ve ekspertiz *(geliştiriliyor)*

Sistemin ayırt edici tarafı **şube bazlı yetki izolasyonu**: bir şube yöneticisi ya da müdür yalnızca kendi şubesinin personelini, aracını ve kayıtlarını görebilir; süper admin ise tüm şubelere erişir.

---

## Teknoloji Yığını

| Katman | Teknoloji |
|---|---|
| Dil / Platform | Java 25, Spring Boot 4.1.0 |
| Web | Spring Web MVC (REST) |
| Veri | Spring Data JPA, Hibernate, PostgreSQL |
| Güvenlik | Spring Security, JJWT 0.12.6, BCrypt |
| Dönüşüm | MapStruct 1.6.3 |
| Doğrulama | Jakarta Bean Validation |
| Yardımcı | Lombok, Spring Boot DevTools |
| E-posta | Spring Boot Starter Mail (yeni personele geçici şifre gönderimi) |
| Derleme | Maven (Maven Wrapper ile birlikte) |

---

## Mimari

Klasik katmanlı mimari; her servis ve controller kendi arayüzü (`interface`) üzerinden tanımlanır.

```
src/main/java/fdn/fdncargallery/
├── config/          # SecurityConfig, AppConfig (AuthenticationProvider, PasswordEncoder)
├── controller/      # REST controller'lar + interfaces/
├── service/         # İş kuralları + interfaces/
├── repository/      # Spring Data JPA repository'leri
├── mapper/          # MapStruct mapper arayüzleri
├── dto/             # Request / Response DTO'ları (modül bazlı paketler)
├── entity/          # JPA entity'leri
├── enums/           # Role, CarStatus, FuelType, BodyType, ...
├── exception/       # BaseException, ErrorMessage, MessageType (hata kodları)
├── handler/         # GlobalExceptionHandler, ApiError, AuthEntryPoint
├── jwt/             # JwtService, JwtAuthenticationFilter
├── seeder/          # DatabaseSeeder (ilk kurulum hesapları)
└── utils/           # UsernameGenerator (kurumsal kullanıcı adı üretimi)
```

**Tasarım kararları**

- İstemciden gelen veriye asla doğrudan güvenilmez: stok girişini yapan personel ve şube bilgisi **token'dan** çözülür, DTO'da böyle bir alan yoktur.
- Tüm iş kuralı ihlalleri `BaseException` + `MessageType` üzerinden tek noktadan HTTP durum koduna çevrilir.
- Silme işlemleri **soft delete**'tir: satır tabloda kalır, `deletedAt` silme zamanını, `deletedBy` silen personeli tutar; böylece geçmiş satış ve stok kayıtları bozulmaz. Personel silindiğinde ayrıca pasife alınır (`active = false`, `terminationDate` damgalanır). Ayrılan personel geri döndüğünde yeni kayıt açılmaz: `reactivate` uçları mevcut satırı canlandırır, böylece kişinin tüm geçmişi tek personel kimliğinde kalır; silinen müşteri de aynı şekilde geri alınır. Şube kapatma ise şubede aktif personel ya da satılmamış araç varsa engellenir.
- Kişisel veri URL'de taşınmaz: TC ile personel ve müşteri aramaları `POST` gövdesiyle yapılır, böylece kimlik numarası erişim log'larına ve hata cevaplarının `path` alanına düşmez.

---

## Domain Modeli

**Vehicle ↔ StockItem ayrımı** projenin temel modelleme kararıdır:

- `Vehicle` — aracın **değişmeyen kimliği** (VIN, marka, model, motor, kasa tipi). Bir fiziksel araç sistemde yalnızca **bir kez** bulunur.
- `StockItem` — aracın galeriden geçtiği **her bir döngü** (plaka, kilometre, renk, liste fiyatı, durum, giriş/çıkış tarihi). Aynı araç yıllar sonra geri gelirse ikinci bir stok kalemi açılır.

Böylece bir aracın geçmişi kaybolmadan, her satış dönemi ayrı ayrı raporlanabilir. Eşzamanlı satışları engellemek için `StockItem` üzerinde `@Version` ile optimistic locking kullanılır.

**Stok girişi araç alımıyla yapılır:** `create_car_purchase` satıcı müşteriyi, alış bilgilerini ve stok kalemini tek işlemde kaydeder. `create_stock_item` yalnızca sisteme geçişten önce galeride bulunan araçlar içindir. Marka ve model serbest metin değildir, `Brand` / `Model` referans tablolarında tanımlı olmalıdır.

**Personel hiyerarşisi**

```
BaseEntity (id, createTime, updateTime, deletedAt, deletedBy)
└── BaseEmployee (kimlik + iletişim + adres + şube + hesap bilgileri + işe giriş/çıkış tarihi)
    ├── SystemAdmin   → SUPER_ADMIN ve BRANCH_ADMIN rolleri
    ├── Manager       → indirim yetkisi, şube satış hedefi, yönetim primi
    └── SalesRep      → prim oranı, aylık satış adedi
```

Kimlik bilgileri ayrı bir hesap tablosunda değil, personelin kendi satırında tutulur: `BaseEmployee` doğrudan `UserDetails` implement eder (`username`, `password`, `email`, `role`, `isFirstLogin`) ve `isEnabled()` `active` alanına bağlıdır. Kullanıcı adı **rol + şube + isim + tarih** formatında otomatik üretilir: `MNG_B1_IkbalK_082026`.

**Diğer entity'ler:** `Branch`, `Customer`, `Brand`, `Model`, `CarPurchase`, `SoldCar`, `Reservation`, `CarMaintenance`, `ExpertReport`, `RefreshToken`. `Address` ayrı bir tablo değil, `@Embeddable` olarak personel / müşteri / şube satırına gömülür.

---

## Güvenlik ve Yetkilendirme

- **Stateless JWT** — access token 15 dakika geçerli; rol, `isFirstLogin`, `branchId` ve `tokenVersion` claim'lerini taşır.
- **Refresh token** — 7 gün geçerli, veritabanında tutulur ve her yenilemede **rotate** edilir (eski token silinir). Süresi dolmuş token'lar her gece 03:00'te temizlenir.
- **İlk giriş zorunluluğu** — hesabı yeni açılan kullanıcı, geçici şifresini değiştirmeden `/api/auth/change-password` dışındaki hiçbir uca erişemez (`JwtAuthenticationFilter` içinde uygulanır). Geçici şifre 24 saat geçerlidir; süresi dolarsa yönetici `resend_temporary_password` ile yenisini gönderir.
- **Şube izolasyonu** — `SecurityService.checkBranchAccess()` ile şube yöneticisi, müdür ve satış temsilcisi yalnızca kendi şubesinin verisine erişir.
- **Oturum iptali** — şifre değişikliği, geçici şifre yenileme, pasife alma ve yeniden işe alımda personelin `tokenVersion` değeri artar ve refresh token'ları silinir; eski access token'lar süresi dolmadan anında geçersizleşir.
- Şifreler **BCrypt** ile hash'lenir; hiçbir uçta düz metin şifre saklanmaz. Yeni şifre 8–72 karakter olmalı; büyük harf, küçük harf, rakam ve özel karakter içermelidir.
- **Loglama** — her log satırına işlemi yapan kullanıcı eklenir (giriş yapılmamış isteklerde `anonim`); loglar `logs/fdn-car-gallery.log` dosyasına yazılır ve 30 gün saklanır.

**Roller:** `SUPER_ADMIN`, `BRANCH_ADMIN`, `MANAGER`, `SALES_REP`

---

## Kurulum

### Gereksinimler

- JDK 25
- PostgreSQL 14+
- Maven (proje Maven Wrapper içerir, ayrıca kurmak zorunda değilsiniz)

### 1. Veritabanı

Uygulama `fdncargallery` şemasını kullanır, şemanın önceden oluşturulmuş olması gerekir:

```sql
CREATE SCHEMA fdncargallery;
```

Tablolar `spring.jpa.hibernate.ddl-auto=update` ile ilk çalıştırmada otomatik oluşur.

### 2. Ortam değişkenleri

Uygulama; JWT anahtarı ve kurulum şifreleri tanımlı değilse **bilinçli olarak başlamaz**. Bu değerler repoya commit'lenmez, ortam değişkeni olarak verilir:

| Değişken | Açıklama |
|---|---|
| `FDN_JWT_SECRET` | Base64 kodlanmış HMAC-SHA256 imzalama anahtarı (zorunlu) |
| `FDN_ADMIN_PASSWORD` | İlk kurulumda oluşturulan sistem yöneticisi şifresi (zorunlu) |
| `FDN_BRANCH_ADMIN_PASSWORD` | İlk kurulumda oluşturulan şube yöneticisinin geçici şifresi (zorunlu) |
| `SPRING_DATASOURCE_PASSWORD` | PostgreSQL şifresi (zorunlu). Adres ve kullanıcı adı yerel varsayılanlarla gelir; farklıysa `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_USERNAME` ile verilir |
| `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` | SMTP hesabı ve şifresi — yeni personelin geçici şifresi bu hesaptan gönderilir |
| `FDN_MAIL_FROM` | Gönderen adresi (opsiyonel; verilmezse `SPRING_MAIL_USERNAME` kullanılır) |

```bash
export FDN_JWT_SECRET="$(openssl rand -base64 32)"
export FDN_ADMIN_PASSWORD="..."
export FDN_BRANCH_ADMIN_PASSWORD="..."
export SPRING_DATASOURCE_PASSWORD="..."
```

Windows PowerShell için:

```powershell
$env:FDN_JWT_SECRET="..."; $env:FDN_ADMIN_PASSWORD="..."; $env:FDN_BRANCH_ADMIN_PASSWORD="..."; $env:SPRING_DATASOURCE_PASSWORD="..."
```

### 3. Çalıştırma

```bash
./mvnw spring-boot:run
```

Uygulama `http://localhost:8080` adresinde ayağa kalkar. İlk açılışta `DatabaseSeeder`, marka/model referans verisini (8 marka, 32 model) yükler ve iki kurulum hesabı ile şubelerini oluşturur:

| Hesap | E-posta | Rol | Şube |
|---|---|---|---|
| Sistem yöneticisi | `admin@fdncargallery.com` | `SUPER_ADMIN` | IT Merkez |
| Şube yöneticisi | `sube.admin@fdncargallery.com` | `BRANCH_ADMIN` | Cyberpark Oto Galeri |

Üretilen kullanıcı adları uygulama log'una yazılır. Şube yöneticisi ilk girişte şifresini değiştirmek zorundadır.

---

## API Uçları

Tüm uçlar `Authorization: Bearer <accessToken>` başlığı bekler (auth uçları hariç). Tablolardaki **tüm roller**: sistem yöneticisi, şube yöneticisi, müdür ve satış temsilcisi. Sistem yöneticisi dışındaki roller yalnızca kendi şubesinin verisini görür.

### Kimlik Doğrulama — `/api/auth`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/login` | Herkese açık |
| `POST` | `/refresh_token` | Herkese açık |
| `POST` | `/logout` | Herkese açık |
| `POST` | `/change-password` | Giriş yapmış kullanıcı |

```http
POST /api/auth/login
Content-Type: application/json

{ "username": "SPR_ADM_B1_SystemA_082026", "password": "..." }
```

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "6c8a...-...",
  "firstLogin": false,
  "tokenType": "Bearer"
}
```

### Şubeler — `/api/branches`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/create_branch` | Sistem yöneticisi |
| `PUT` | `/update_branch/{id}` | Sistem yöneticisi, şube yöneticisi |
| `GET` | `/list_branch` · `/list_branch/{id}` | Sistem yöneticisi, şube yöneticisi, müdür |
| `DELETE` | `/delete_branch/{id}` | Sistem yöneticisi |

### Şube Yöneticileri — `/api/branch-admins`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/create_branch_admin` | Sistem yöneticisi |
| `PUT` | `/update_branch_admin/{id}` | Sistem yöneticisi |
| `GET` | `/list_branch_admin` · `/list_branch_admin/{id}` | Sistem yöneticisi, şube yöneticisi (yalnızca kendisi) |
| `DELETE` | `/delete_branch_admin/{id}` | Sistem yöneticisi *(pasife alır)* |
| `PUT` | `/reactivate_branch_admin/{id}` | Sistem yöneticisi *(pasif kaydı geri açar)* |

### Müdürler — `/api/managers`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/create_manager` | Sistem yöneticisi, şube yöneticisi |
| `PUT` | `/update_manager/{id}` | Sistem yöneticisi, şube yöneticisi |
| `GET` | `/list_manager` · `/list_manager/{id}` | Sistem yöneticisi, şube yöneticisi, müdür |
| `DELETE` | `/delete/{id}` | Sistem yöneticisi, şube yöneticisi *(pasife alır)* |
| `PUT` | `/reactivate_manager/{id}` | Sistem yöneticisi, şube yöneticisi *(pasif kaydı geri açar)* |

### Satış Temsilcileri — `/api/salesRep`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/create_salesRep` | Sistem yöneticisi, şube yöneticisi, müdür |
| `PUT` | `/update_salesRep/{id}` | Sistem yöneticisi, şube yöneticisi, müdür |
| `GET` | `/list_allSalesRep` | Sistem yöneticisi, şube yöneticisi, müdür |
| `GET` | `/list_salesRep/{id}` | Sistem yöneticisi, şube yöneticisi, müdür, satış temsilcisi *(yalnızca kendisi)* |
| `DELETE` | `/delete_salesRep/{id}` | Sistem yöneticisi, şube yöneticisi, müdür *(pasife alır)* |
| `PUT` | `/reactivate_salesRep/{id}` | Sistem yöneticisi, şube yöneticisi, müdür *(pasif kaydı geri açar)* |

### Personel (ortak) — `/api/employees`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/search_employee` | Sistem yöneticisi, şube yöneticisi, müdür |
| `POST` | `/resend_temporary_password` | Sistem yöneticisi, şube yöneticisi, müdür |

> TC kimlik numarasıyla **rolden bağımsız** personel araması. Ayrılmış personelin kaydını bulup yeniden işe alım için gereken `id`'yi verir; pasif kayıtlar listeleme uçlarında görünmediği için bu uç olmadan bulunamazlar. Dönen sonuç bilinçli olarak dardır: `id`, ad, soyad, rol, `active`, işe giriş/çıkış tarihi ve şube adı — maaş, adres, iletişim ve hesap bilgileri yer almaz.

> `resend_temporary_password` geçici şifre e-postası ulaşmayan ya da şifresinin süresi dolan personele yeni geçici şifre üretip gönderir (TC gövdede). Kayıtlı şifre BCrypt hash'i olduğu için eskisi gönderilemez, her seferinde yenisi üretilir. Şube yöneticisi müdüre ve satış temsilcisine, müdür yalnızca satış temsilcisine gönderebilir.

### Müşteriler — `/api/customers`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/create_customer` | Tüm roller |
| `PUT` | `/update_customer/{id}` | Tüm roller |
| `GET` | `/list_customer` · `/list_customer/{id}` | Tüm roller |
| `POST` | `/search_customer` | Tüm roller |
| `DELETE` | `/delete_customer/{id}` | Sistem yöneticisi, şube yöneticisi, müdür *(soft delete)* |
| `PUT` | `/reactivate_customer/{id}` | Sistem yöneticisi, şube yöneticisi, müdür *(silinen kaydı geri alır)* |

> Müşteri şubeye bağlı değildir, tüm şubeler aynı kaydı kullanır. Bireysel müşteri 11 haneli TCKN, kurumsal müşteri 10 haneli VKN ile kaydolur. `search_customer` silinmiş kayıtları da döner; geri alınacak kaydın `id`'si buradan bulunur.

### Marka ve Model — `/api/brands`, `/api/models`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/api/brands/create_brand` | Sistem yöneticisi, şube yöneticisi |
| `GET` | `/api/brands/list_brand` | Tüm roller |
| `POST` | `/api/models/create_model` | Sistem yöneticisi, şube yöneticisi, müdür |
| `GET` | `/api/models/list_model` · `/api/models/list_model/brand/{brandId}` | Tüm roller |

### Stok Kalemleri — `/api/stock-items`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/create_stock_item` | Sistem yöneticisi, şube yöneticisi, müdür |
| `PUT` | `/update_stock_item/{id}` | Sistem yöneticisi, şube yöneticisi, müdür |
| `GET` | `/list_stock_item` · `/list_stock_item/{id}` | Tüm roller |
| `DELETE` | `/delete_stock_item/{id}` | Sistem yöneticisi, şube yöneticisi, müdür *(soft delete)* |

> Normal stok girişi `/api/car-purchases/create_car_purchase` ile yapılır; `create_stock_item` sisteme geçişten önce galeride bulunan araçlar içindir. Satılmış stok kalemi güncellenemez ve silinemez.

### Araçlar — `/api/vehicles`

| Method | Uç | Erişim |
|---|---|---|
| `PUT` | `/update_vehicle/{id}` | Sistem yöneticisi, şube yöneticisi, müdür |
| `GET` | `/list_vehicle/{id}` | Sistem yöneticisi, şube yöneticisi, müdür |

> Araç kaydı ayrı bir uçtan açılmaz; stok girişi sırasında VIN'e göre ya mevcut araç yeniden kullanılır ya da yeni bir `Vehicle` oluşturulur. Model yılı, içinde bulunulan yılın bir fazlasını geçemez.

### Araç Alımları — `/api/car-purchases`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/create_car_purchase` | Tüm roller |
| `PUT` | `/update_car_purchase/{id}` | Tüm roller |
| `GET` | `/list_car_purchase` · `/list_car_purchase/{id}` | Tüm roller |

> Satıcı müşteri önce `search_customer` ile bulunur ya da `create_customer` ile açılır, `id`'si `sellerCustomerId` olarak gönderilir; alış kaydı stok kalemini aynı işlemde açar. Satış temsilcisi yalnızca kendi yaptığı alışları görür ve günceller. Silme ucu yoktur.

### Bakım — `/api/car-maintenances`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/create_car_maintenance` | Tüm roller |
| `PUT` | `/update_car_maintenance/{id}` | Tüm roller |
| `PUT` | `/complete_car_maintenance/{id}` | Tüm roller |
| `GET` | `/list_car_maintenance` · `/list_car_maintenance/{id}` | Tüm roller |
| `DELETE` | `/delete_car_maintenance/{id}` | Tüm roller |

> Yalnızca satışta (`AVAILABLE`) olan araç bakıma alınabilir; bakım açılınca araç `IN_MAINTENANCE` olur ve yalnızca `complete_car_maintenance` ile satışa döner. `expectedEndDate` aracın durumunu değiştirmez; gerçek teslim günü gövdede `completedAt` ile verilir, boşsa bugün yazılır. Tamamlanmış bakım değiştirilemez; yalnızca henüz tamamlanmamış bakım silinebilir. Satış temsilcisi yalnızca kendi açtığı bakım kayıtlarına erişir.

---

## Hata Formatı

Tüm hatalar `GlobalExceptionHandler` üzerinden tek tip döner:

```json
{
  "statusCode": 409,
  "errorCode": "7008",
  "exception": {
    "path": "/api/stock-items/create_stock_item",
    "creationDate": "28-08-2026 21:14:03",
    "message": "Bu plakayla açık bir stok kaydı bulunuyor. : 06 ABC 123"
  }
}
```

Hata kodları `MessageType` enum'ında gruplanmıştır:

| Aralık | Kapsam |
|---|---|
| `1000` | Genel ve doğrulama hataları |
| `2000` | Veritabanı / kayıt hataları |
| `3000` | Kimlik doğrulama, token ve şifre hataları |
| `5000` | Şube ve personel iş kuralları |
| `6000` | Bakım ve ekspertiz |
| `7000` | Araç kimliği ve stok kalemi |
| `8000` | Müşteri, adres ve müdür |

---

## 🗺 Yol Haritası

### Tamamlananlar

- [x] JWT tabanlı kimlik doğrulama (login / refresh + rotation / logout / şifre değiştirme)
- [x] İlk girişte zorunlu şifre değişimi
- [x] Şube CRUD + adres yönetimi
- [x] Şube yöneticisi CRUD (şube başına tek yönetici kuralı)
- [x] Müdür CRUD (şube başına tek müdür kuralı)
- [x] Stok kalemi CRUD, şubeler arası transfer, VIN/plaka tekilliği
- [x] Şube bazlı yetki izolasyonu
- [x] Merkezî hata yönetimi ve hata kodu sözlüğü
- [x] Otomatik kurumsal kullanıcı adı üretimi
- [x] İlk kurulum seeder'ı
- [x] Email gönderme servisi entegrasyonu
- [x] Kalıtımın tek tabloya indirilmesi (`SINGLE_TABLE` + `employee_type` discriminator)
- [x] Adresin `@Embeddable` yapılması, hesap tablosunun personelle birleştirilmesi
- [x] Ayrılan personelin yeniden işe alınması (`reactivate` uçları, `terminationDate` takibi)
- [x] TC ile rolden bağımsız personel arama
- [x] Satış temsilcisi CRUD ve yeniden işe alım
- [x] Marka / model referans verisi
- [x] Müşteri kaydı (TCKN / VKN doğrulaması), TC ile arama ve silinen kaydı geri alma
- [x] Araç alımı (müşteriden alımla stok girişi)
- [x] Araç bakımı (bakıma alma, tamamlama, aracın satışa dönmesi)
- [x] Soft delete ve silen personelin kaydı (`deletedBy`)
- [x] Şifre değişikliği ve pasife almada oturum iptali (`tokenVersion`)
- [x] Süresi dolmuş refresh token'ların gece temizliği
- [x] Şifre politikası ve 24 saat geçerli geçici şifre
- [x] Geçici şifrenin yeniden gönderilmesi
- [x] Kullanıcı bazlı loglama
- [x] Veritabanı bağlantı bilgilerinin ortam değişkenlerine taşınması

### Devam eden / planlanan

- [ ] **Araç satış (SoldCar)** akışı — prim oranının satış anında dondurulması, müdür indirim limiti
- [ ] **Rezervasyon (Reservation)** akışı
- [ ] **Ekspertiz (ExpertReport)** modülü
- [ ] Listeleme uçlarına sayfalama, sıralama ve filtreleme
- [ ] Swagger / OpenAPI dokümantasyonu
- [ ] Birim ve entegrasyon testleri (şu an yalnızca context testi mevcut)

---

## Katkı

Proje şu an tek geliştiricili ve aktif geliştirme aşamasında olduğu için dış katkıya kapalıdır. Öneri ve geri bildirimlerinizi **issue** açarak iletebilirsiniz.

---

## Geliştirici

**İkbal Kolay** — [@ikbalkly](https://github.com/ikbalkly)
