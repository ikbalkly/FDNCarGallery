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
> - Şu an **kimlik doğrulama, şube, şube yöneticisi, müdür ve araç stoğu** modülleri çalışır durumda.
> - Müşteri, alım, satış, bakım ve ekspertiz modüllerinin **entity / DTO / mapper katmanları hazır**, servis ve controller katmanları yazılıyor.
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
- **Alım / satış süreçleri** — müşteriden araç alımı, satış, prim hesabı, bakım ve ekspertiz kayıtları *(geliştiriliyor)*

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
| E-posta | Spring Boot Starter Mail *(bağımlılık eklendi, henüz kullanılmıyor)* |
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
- Silme işlemleri personel tarafında **soft delete**'tir (`active = false`), böylece geçmiş satış ve stok kayıtları bozulmaz. Şube silme ise personel/araç varsa engellenir.

---

## Domain Modeli

**Vehicle ↔ StockItem ayrımı** projenin temel modelleme kararıdır:

- `Vehicle` — aracın **değişmeyen kimliği** (VIN, marka, model, motor, kasa tipi). Bir fiziksel araç sistemde yalnızca **bir kez** bulunur.
- `StockItem` — aracın galeriden geçtiği **her bir döngü** (plaka, kilometre, renk, liste fiyatı, durum, giriş/çıkış tarihi). Aynı araç yıllar sonra geri gelirse ikinci bir stok kalemi açılır.

Böylece bir aracın geçmişi kaybolmadan, her satış dönemi ayrı ayrı raporlanabilir. Eşzamanlı satışları engellemek için `StockItem` üzerinde `@Version` ile optimistic locking kullanılır.

**Personel hiyerarşisi**

```
BaseEntity (id, createTime, updateTime)
└── BaseEmployee (ad, soyad, TC, telefon, maaş, adres, şube, aktiflik)
    ├── SystemAdmin   → SUPER_ADMIN ve BRANCH_ADMIN rolleri
    ├── Manager       → indirim yetkisi, şube satış hedefi, yönetim primi
    └── SalesRep      → prim oranı, aylık satış adedi
```

Her personelin bir `UserAccount` kaydı vardır (`UserDetails` implementasyonu); kullanıcı adı **rol + şube + isim + tarih** formatında otomatik üretilir: `MNG_B1_IkbalK_082026`.

**Diğer entity'ler:** `Branch`, `Address`, `Customer`, `CarPurchase`, `SoldCar`, `CarMaintenance`, `ExpertReport`, `RefreshToken`.

---

## Güvenlik ve Yetkilendirme

- **Stateless JWT** — access token 15 dakika geçerli; rol, `isFirstLogin` ve `branchId` claim'lerini taşır.
- **Refresh token** — 7 gün geçerli, veritabanında tutulur ve her yenilemede **rotate** edilir (eski token silinir).
- **İlk giriş zorunluluğu** — hesabı yeni açılan kullanıcı, geçici şifresini değiştirmeden `/api/auth/change-password` dışındaki hiçbir uca erişemez (`JwtAuthenticationFilter` içinde uygulanır).
- **Şube izolasyonu** — `SecurityService.checkBranchAccess()` ile şube yöneticisi ve müdür yalnızca kendi şubesinin verisine erişir.
- **Pasif hesap kontrolü** — `active = false` yapılan personelin token'ı anında geçersizleşir.
- Şifreler **BCrypt** ile hash'lenir; hiçbir uçta düz metin şifre saklanmaz.

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
| `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` | SMTP bilgileri (opsiyonel, e-posta modülü henüz aktif değil) |

```bash
export FDN_JWT_SECRET="$(openssl rand -base64 32)"
export FDN_ADMIN_PASSWORD="..."
export FDN_BRANCH_ADMIN_PASSWORD="..."
```

Windows PowerShell için:

```powershell
$env:FDN_JWT_SECRET="..."; $env:FDN_ADMIN_PASSWORD="..."; $env:FDN_BRANCH_ADMIN_PASSWORD="..."
```

### 3. Çalıştırma

```bash
./mvnw spring-boot:run
```

Uygulama `http://localhost:8080` adresinde ayağa kalkar. İlk açılışta `DatabaseSeeder`, iki kurulum hesabı ve şubelerini oluşturur:

| Hesap | E-posta | Rol | Şube |
|---|---|---|---|
| Sistem yöneticisi | `admin@fdncargallery.com` | `SUPER_ADMIN` | IT Merkez |
| Şube yöneticisi | `sube.admin@fdncargallery.com` | `BRANCH_ADMIN` | Cyberpark Oto Galeri |

Üretilen kullanıcı adları uygulama log'una yazılır. Şube yöneticisi ilk girişte şifresini değiştirmek zorundadır.

---

## API Uçları

Tüm uçlar `Authorization: Bearer <accessToken>` başlığı bekler (auth uçları hariç).

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

### Müdürler — `/api/managers`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/create_manager` | Sistem yöneticisi, şube yöneticisi |
| `PUT` | `/update_manager/{id}` | Sistem yöneticisi, şube yöneticisi |
| `GET` | `/list_manager` · `/list_manager/{id}` | Sistem yöneticisi, şube yöneticisi, müdür |
| `DELETE` | `/delete/{id}` | Sistem yöneticisi, şube yöneticisi *(pasife alır)* |

### Stok Kalemleri — `/api/stock-items`

| Method | Uç | Erişim |
|---|---|---|
| `POST` | `/create_stock_item` | Sistem yöneticisi, şube yöneticisi, müdür |
| `PUT` | `/update_stock_item/{id}` | Sistem yöneticisi, şube yöneticisi, müdür |
| `GET` | `/list_stock_item` · `/list_stock_item/{id}` | Sistem yöneticisi, şube yöneticisi, müdür |
| `DELETE` | `/delete_stock_item/{id}` | Sistem yöneticisi, şube yöneticisi, müdür |

### Araçlar — `/api/vehicles`

| Method | Uç | Erişim |
|---|---|---|
| `PUT` | `/update_vehicle/{id}` | Sistem yöneticisi, şube yöneticisi, müdür |
| `GET` | `/list_vehicle/{id}` | Sistem yöneticisi, şube yöneticisi, müdür |

> Araç kaydı ayrı bir uçtan açılmaz; stok girişi sırasında VIN'e göre ya mevcut araç yeniden kullanılır ya da yeni bir `Vehicle` oluşturulur.

---

## Hata Formatı

Tüm hatalar `GlobalExceptionHandler` üzerinden tek tip döner:

```json
{
  "statusCode": 409,
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
- [x] Email Servisi Entegrasyonu

### Devam eden / planlanan

- [ ] **Satış temsilcisi (SalesRep)** modülü — DTO ve mapper hazır, servis + controller yazılacak
- [ ] **Müşteri (Customer)** modülü — bireysel / kurumsal TCKN-VKN doğrulaması dahil
- [ ] **Araç alım (CarPurchase)** akışı — müşteriden alım, stok kalemi oluşturma
- [ ] **Araç satış (SoldCar)** akışı — prim oranının satış anında dondurulması, müdür indirim limiti
- [ ] **Bakım (CarMaintenance)** ve **ekspertiz (ExpertReport)** modülleri
- [ ] Kalıtım stratejisinin `TABLE_PER_CLASS` → `JOINED` olarak değiştirilmesi
- [ ] Veritabanı bağlantı bilgilerinin ortam değişkenlerine taşınması
- [ ] Listeleme uçlarına sayfalama, sıralama ve filtreleme
- [ ] Swagger / OpenAPI dokümantasyonu
- [ ] Birim ve entegrasyon testleri (şu an yalnızca context testi mevcut)

---

## Katkı

Proje şu an tek geliştiricili ve aktif geliştirme aşamasında olduğu için dış katkıya kapalıdır. Öneri ve geri bildirimlerinizi **issue** açarak iletebilirsiniz.

---

## Geliştirici

**İkbal Kolay** — [@ikbalkly](https://github.com/ikbalkly)
