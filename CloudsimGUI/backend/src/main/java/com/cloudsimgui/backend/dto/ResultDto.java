package com.cloudsimgui.backend.dto;

import com.cloudsimgui.backend.enums.Statuses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {
    private String jobId;
    private double totalExecutionTime;
    private double totalCpuCost;
    private int totalCloudlets;
    private int successfulCloudlets;
    private List<CloudletExecutionResultDto> cloudletResults;
}
