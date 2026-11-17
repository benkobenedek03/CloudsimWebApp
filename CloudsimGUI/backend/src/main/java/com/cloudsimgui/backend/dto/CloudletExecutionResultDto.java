package com.cloudsimgui.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudletExecutionResultDto {
    private long cloudletId;
    private String status;
    private long vmId;
    private double execTime;
    private double startTime;
    private double finishTime;
}
