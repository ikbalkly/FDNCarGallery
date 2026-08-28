package fdn.fdncargallery.enums;

import lombok.Getter;

@Getter
public enum Role {

    SUPER_ADMIN("SPR_ADM"), // süper admin -> her şeye erişebilir
    BRANCH_ADMIN("BRC_ADM"), // şube admini -> sadece kendi şubesine
    MANAGER("MNG"),    // müdür
    SALES_REP("SL_REP");   // satış temsilcisi

    private final String usernamePrefix;

    Role(String usernamePrefix) {
        this.usernamePrefix = usernamePrefix;

    }
}
