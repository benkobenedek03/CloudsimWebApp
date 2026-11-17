package com.cloudsimgui.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudletDto {
    private int id;

    @Builder.Default
    private long length = 10000; // MI (Million Instructions)

    @Builder.Default
    private int pes = 1;

    @Builder.Default
    private long fileSize = 300; // MB

    @Builder.Default
    private long outputSize = 300; // MB
}