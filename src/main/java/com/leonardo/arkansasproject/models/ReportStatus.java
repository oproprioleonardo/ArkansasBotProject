package com.leonardo.arkansasproject.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public enum ReportStatus {

    @SerializedName("ARCHIVED")
    ARCHIVED("Arquivado"),
    @SerializedName("ACTIVE")
    ACTIVATED("Em análise"),
    @SerializedName("ACCEPTED")
    ACCEPTED("Aprovado"),
    @SerializedName("REFUSED")
    REFUSED("Recusado");

    @Getter
    private final String value;

    ReportStatus(String value) {
        this.value = value;
    }

}
