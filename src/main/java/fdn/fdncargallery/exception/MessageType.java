package fdn.fdncargallery.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MessageType {

    // mesaj tipleri değiştirilebilir

    GENERAL_EXCEPTION("1000", "Bir hata oluştu.", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR("1001", "Girilen veriler kurallara uymuyor.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_OPERATION("1002", "Desteklenmeyen işlem.", HttpStatus.METHOD_NOT_ALLOWED),

    // --- 2000: Veritabanı ve Kayıt Hataları (CRUD) ---
    NO_RECORD_EXIST("2000", "Kayıt bulunamadı.", HttpStatus.NOT_FOUND),
    ALREADY_EXISTS("2001", "Bu kayıt sistemde zaten mevcut.", HttpStatus.CONFLICT),
    DATA_INTEGRITY_VIOLATION("2002", "Veri bütünlüğü hatası, ilişkisel kısıtlamalara takıldı.", HttpStatus.CONFLICT),
    CONCURRENT_MODIFICATION("2003", "Bu kayıt siz işlem yaparken başkası tarafından değiştirildi. Lütfen sayfayı yenileyip tekrar deneyin.", HttpStatus.CONFLICT),

    // --- 3000: Kimlik Doğrulama ve Güvenlik Hataları (Auth & JWT) ---
    // Kimlik doğrulanamadı -> 401, kimlik var ama yetki yok -> 403
    BAD_CREDENTIALS("3000", "Kullanıcı adı veya şifre hatalı.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("3001", "Bu işlem için yetkiniz bulunmuyor.", HttpStatus.FORBIDDEN),
    TOKEN_IS_EXPIRE("3002", "Token süresi doldu, lütfen tekrar giriş yapın.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("3003", "Oturum süreniz doldu, lütfen tekrar giriş yapın.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("3004", "Geçersiz token.", HttpStatus.UNAUTHORIZED),
    USERNAME_ALREADY_EXISTS("3005", "Bu kullanıcı adı başka bir hesap tarafından kullanılıyor.", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("3006", "Bu e-posta adresi başka bir hesap tarafından kullanılıyor.", HttpStatus.CONFLICT),
    PASSWORD_CONFIRMATION_MISMATCH("3007", "Yeni şifre ile tekrarı eşleşmiyor.", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_OLD("3008", "Yeni şifre mevcut şifrenizle aynı olamaz.", HttpStatus.BAD_REQUEST),
    ACCOUNT_DISABLED("3009", "Hesabınız pasif durumda, lütfen yöneticinizle görüşün.", HttpStatus.FORBIDDEN),
    PASSWORD_CHANGE_REQUIRED("3010", "Sistemi kullanabilmek için önce size verilen varsayılan şifrenizi değiştirmelisiniz.", HttpStatus.FORBIDDEN),
    // Kimlik hiç doğrulanmadı (token yok / geçersiz). UNAUTHORIZED'dan farkı:
    // orada kimlik var ama yetki yok (403), burada kimlik yok (401).
    AUTHENTICATION_REQUIRED("3011", "Bu işlem için giriş yapmanız gerekiyor.", HttpStatus.UNAUTHORIZED),

    //--- 5000: Şube ve personel için gerekli kurallar
    BRANCH_NOT_FOUND("5000", "Belirtilen şube bulunamadı.", HttpStatus.NOT_FOUND),
    MANAGER_ALREADY_ASSIGNED("5001", "Bu şubeye zaten bir müdür atanmış durumda. Yeni atama yapmadan önce mevcut müdürü görevden almalısınız.", HttpStatus.CONFLICT),
    EMPLOYEE_NOT_FOUND("5002", "Belirtilen personel sistemde bulunamadı.", HttpStatus.NOT_FOUND),
    INVALID_EMPLOYEE_ROLE("5003", "Bu personelin rolü, yapılmak istenen işlem için uygun değil.", HttpStatus.BAD_REQUEST),
    EMPLOYEE_NOT_IN_BRANCH("5004", "Personel bu şubede görev yapmamaktadır.", HttpStatus.BAD_REQUEST),
    EMPLOYEE_NOT_ACTIVE("5005", "Bu personel pasif durumda, işleme dahil edilemez.", HttpStatus.CONFLICT),
    BRANCH_HAS_EMPLOYEES("5006", "Şubede kayıtlı personel var, önce personelleri başka şubeye taşıyın.", HttpStatus.CONFLICT),
    BRANCH_HAS_STOCK("5007", "Şubede kayıtlı araç var, önce araçları başka şubeye taşıyın.", HttpStatus.CONFLICT),
    BRANCH_ADMIN_NOT_FOUND("5008", "Belirtilen şube yöneticisi sistemde bulunamadı.", HttpStatus.NOT_FOUND),
    BRANCH_ADMIN_ALREADY_ASSIGNED("5009", "Bu şubenin zaten bir yöneticisi var. Yeni yönetici atamadan önce mevcut yöneticiyi görevden almalısınız.", HttpStatus.CONFLICT),
    // Bir kişinin sistemde tek personel kaydı olabilir: aynı TC ikinci bir rolde açılamaz.
    EMPLOYEE_IDENTITY_ALREADY_EXISTS("5010", "Bu TC kimlik numarası başka bir personel kaydında kullanılıyor. Bir kişi yalnızca tek bir rolde görev yapabilir.", HttpStatus.CONFLICT),

    // --- 6000: Bakım ve ekspertiz ---
    STOCK_ITEM_ALREADY_IN_MAINTENANCE("6000", "Bu araç şu anda aktif olarak bakımda görünmektedir, yeni bakım kaydı açılamaz.", HttpStatus.CONFLICT),
    MAINTENANCE_NOT_FOUND("6001", "Araç bakım kaydı bulunamadı.", HttpStatus.NOT_FOUND),
    STOCK_ITEM_UNDER_MAINTENANCE_CANNOT_BE_SOLD("6002", "Araç şu anda bakımda olduğu için satış işlemi gerçekleştirilemez.", HttpStatus.CONFLICT),
    EXPERT_REPORT_REQUIRED("6003", "Bu aracın satış/alım işlemini tamamlamak için güncel bir ekspertiz raporu zorunludur.", HttpStatus.CONFLICT),
    EXPERT_REPORT_NOT_FOUND("6004", "Ekspertiz raporu bulunamadı.", HttpStatus.NOT_FOUND),

    // --- 7000: Araç kimliği (Vehicle) ve stok kalemi (StockItem) ---
    VEHICLE_NOT_FOUND("7000", "Belirtilen araç (VIN) sistemde bulunamadı.", HttpStatus.NOT_FOUND),
    STOCK_ITEM_NOT_AVAILABLE_FOR_SALE("7001", "Bu stok kalemi şu anda satılık statüsünde değil.", HttpStatus.CONFLICT),
    SOLD_STOCK_ITEM_CANNOT_BE_MODIFIED("7002", "Satışı tamamlanmış bir stok kalemi üzerinde bilgi güncellemesi yapılamaz.", HttpStatus.CONFLICT),
    PURCHASE_RECORD_NOT_FOUND("7003", "Araç alım (Purchase) kaydı sistemde bulunamadı.", HttpStatus.NOT_FOUND),
    SALE_RECORD_NOT_FOUND("7004", "Araç satış (Sold) kaydı sistemde bulunamadı.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK("7005", "Bu işlem için şube stoklarında yeterli araç bulunmuyor.", HttpStatus.CONFLICT),
    STOCK_ITEM_NOT_FOUND("7006", "Belirtilen stok kalemi sistemde bulunamadı.", HttpStatus.NOT_FOUND),
    VEHICLE_ALREADY_IN_STOCK("7007", "Bu araç için halihazırda açık bir stok kaydı bulunuyor. Aynı araç aynı anda iki kez stoğa alınamaz.", HttpStatus.CONFLICT),
    PLATE_ALREADY_IN_STOCK("7008", "Bu plakayla açık bir stok kaydı bulunuyor.", HttpStatus.CONFLICT),

    CUSTOMER_NOT_FOUND("8000", "Belirtilen müşteri sistemde bulunamadı.", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_FOUND("8001", "Adres bilgisi bulunamadı.", HttpStatus.NOT_FOUND),
    MANAGER_NOT_FOUND("8002", "Belirtilen manager sistemde bulunamadı.", HttpStatus.NOT_FOUND),
    INVALID_IDENTITY_NUMBER("8003", "Kimlik numarası müşteri tipiyle uyuşmuyor. Bireysel için 11 haneli TCKN, kurumsal için 10 haneli VKN girilmelidir.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    MessageType(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
