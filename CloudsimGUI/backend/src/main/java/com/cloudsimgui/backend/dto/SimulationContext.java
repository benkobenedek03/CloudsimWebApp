package com.cloudsimgui.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.core.CloudSimPlus;

@AllArgsConstructor
@Data
public class SimulationContext {
    private CloudSimPlus simulation;
    private DatacenterBroker broker;
    private int totalCloudletCount;
}
