package com.cloudsimgui.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostDto {
    @Builder.Default
    private int pes = 8;

    @Builder.Default
    private long mips = 10000;

    @Builder.Default
    private long ram = 16384; // MB

    @Builder.Default
    private long storage = 1000000; // MB

    @Builder.Default
    private long bw = 10000; // Mbps

    @Builder.Default
    private String vmScheduler = "TIME_SHARED"; // TIME_SHARED, SPACE_SHARED vagy CUSTOM
    private String customSchedulerClass;
}
