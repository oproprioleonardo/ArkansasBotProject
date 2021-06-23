package com.leonardo.arkansasproject.models.suppliers;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.Arrays;

public enum ReportProcessingState {

    @SerializedName("ATTACH_STEP_BY_STEP")
    ATTACH_STEP_BY_STEP(1, "Anexar passo a passo"),
    @SerializedName("ATTACH_EXPECTED_RESULT")
    ATTACH_EXPECTED_RESULT(2, "Anexar resultado esperado"),
    @SerializedName("ATTACH_ACTUAL_RESULT")
    ATTACH_ACTUAL_RESULT(3, "Anexar resultado atual"),
    @SerializedName("ATTACH_SERVER")
    ATTACH_SERVER(4, "Anexar servidor");

    @Getter
    private final int position;
    @Getter
    private final String value;

    ReportProcessingState(int position, String value) {
        this.value = value;
        this.position = position;
    }

    public static ReportProcessingState fromPosition(int position) {
        return Arrays.stream(ReportProcessingState.values()).filter(state -> state.getPosition() == position)
                     .findFirst().orElse(null);
    }

    public int next() {
        return this.position + 1;
    }

    public boolean hasNext() {
        return Arrays.stream(values()).anyMatch(state -> state.getPosition() > this.position);
    }

    public ReportProcessingState nextState() {
        return fromPosition(next());
    }
}
