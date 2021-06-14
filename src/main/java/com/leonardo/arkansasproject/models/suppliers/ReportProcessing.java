package com.leonardo.arkansasproject.models.suppliers;

import com.leonardo.arkansasproject.models.Report;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportProcessing {

    private Report report;
    private ReportProcessingState processingState = ReportProcessingState.ATTACH_STEP_BY_STEP;
    private Message message;



}
