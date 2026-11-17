package com.cloudsimgui.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleRequestDTO {
    @Builder.Default
    private int datacenterCount = 1;

    @Builder.Default
    private int hostCount = 2;

    @Builder.Default
    private int vmCount = 4;

    @Builder.Default
    private int cloudletCount = 10;

    @Builder.Default
    private long hostMips = 10000;

    @Builder.Default
    private int hostPes = 4;

    @Builder.Default
    private long hostRam = 16384; // MB

    @Builder.Default
    private long vmMips = 2000;

    @Builder.Default
    private int vmPes = 2;

    @Builder.Default
    private long vmRam = 4096; // MB

    @Builder.Default
    private long cloudletLength = 10000; // MI

    @Builder.Default
    private int cloudletPes = 1;

    @Builder.Default
    private String schedulingPolicy = "TIME_SHARED";
}
