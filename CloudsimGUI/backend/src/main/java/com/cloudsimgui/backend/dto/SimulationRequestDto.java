package com.cloudsimgui.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationRequestDto {
    //private String name;
    //private String mode;
    private SimpleRequestDTO simpleRequest;
    private DetailedRequestDto detailedRequest;
}