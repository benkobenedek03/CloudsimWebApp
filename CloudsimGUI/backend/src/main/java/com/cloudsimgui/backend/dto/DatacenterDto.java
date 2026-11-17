package com.cloudsimgui.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatacenterDto {
    @Builder.Default
    private String name = "Datacenter_1";

    @Builder.Default
    private double costPerSec = 3.0;

    @Builder.Default
    private double costPerMem = 0.05;

    @Builder.Default
    private double costPerStorage = 0.001;

    @Builder.Default
    private double costPerBw = 0.0;

    private List<HostDto> hosts;
}