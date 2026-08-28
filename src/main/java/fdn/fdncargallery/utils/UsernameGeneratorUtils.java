package fdn.fdncargallery.utils;

import fdn.fdncargallery.enums.Role;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class UsernameGeneratorUtils {

    private static final DateTimeFormatter DATE_SUFFIX = DateTimeFormatter.ofPattern("MMyyyy");

    private UsernameGeneratorUtils() {
        // yardımcı sınıf, örneklenmesin
    }

    /**
     * Role, isme, soyisme, şube ID'sine ve tarihe göre kurumsal kullanıcı adı üretir.
     * Örnek çıktı: MNG_B1_IkbalK_082026
     *
     * Not: Benzersizlik GARANTİ ETMEZ. Aynı şubede aynı ay içinde aynı isimli iki kişi
     * aynı sonucu üretir; çakışma kontrolü UsernameGenerator bean'inde yapılır.
     */
    public static String generateCorporateUsername(String firstName, String lastName, Role role, Long branchId) {

        String prefix = role != null ? role.getUsernamePrefix() : "EMP";

        // Şubesiz kişiler (ör. sistem admini) için B0
        String branchCode = "B" + (branchId != null ? branchId : "0");

        String cleanName = cleanString(firstName);
        String cleanLastName = cleanString(lastName);

        String lastNameInitial = !cleanLastName.isEmpty()
                ? cleanLastName.substring(0, 1).toUpperCase(Locale.ROOT)
                : "";

        String dateSuffix = LocalDate.now().format(DATE_SUFFIX);

        return prefix + "_" + branchCode + "_" + cleanName + lastNameInitial + "_" + dateSuffix;
    }

    /**
     * Türkçe karakterleri ASCII'ye indirger ve boşlukları siler: "İkbal" -> "Ikbal"
     * Locale.ROOT şart: Türkçe locale'de toLowerCase() "I" harfini "ı" yapar.
     */
    private static String cleanString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String noSpaces = input.trim().replaceAll("\\s+", "");
        String normalized = Normalizer.normalize(noSpaces, Normalizer.Form.NFD);
        String noAccents = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        if (noAccents.isEmpty()) {
            return "";
        }

        return noAccents.substring(0, 1).toUpperCase(Locale.ROOT)
                + noAccents.substring(1).toLowerCase(Locale.ROOT);
    }
}