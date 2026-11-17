package com.cloudsimgui.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmDto {
    private int id;

    @Builder.Default
    private long mips = 1000;

    @Builder.Default
    private int pes = 2;

    @Builder.Default
    private long ram = 2048; // MB

    @Builder.Default
    private long bw = 1000; // Mbps

    @Builder.Default
    private long size = 10000; // MB (Image méret)

    @Builder.Default
    private String cloudletScheduler = "TIME_SHARED"; // TIME_SHARED, SPACE_SHARED vagy CUSTOM
    private String customSchedulerClass;
}