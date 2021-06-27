package com.leonardo.arkansasproject.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public enum ReportState {

    @SerializedName("ARCHIVED")
    ARCHIVED("Arquivado"),
    @SerializedName("ACTIVE")
    ACTIVE("Em análise"),
    @SerializedName("ACCEPTED")
    ACCEPTED("Aprovado"),
    @SerializedName("REFUSED")
    REFUSED("Recusado");

    @Getter
    private final String value;

    ReportState(String value) {
        this.value = value;
    }

}
