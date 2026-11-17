package com.cloudsimgui.backend.services;

import com.cloudsimgui.backend.dto.CloudletExecutionResultDto;
import com.cloudsimgui.backend.dto.ResultDto;
import com.cloudsimgui.backend.dto.SimulationContext;
import com.cloudsimgui.backend.enums.Statuses;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimulationRunnerService {
    CacheService cache;

    public SimulationRunnerService(CacheService cache) {
        this.cache = cache;
    }
    @Async
    public void RunSimulation(SimulationContext simulation, String Id){
        try{
            cache.AddStatus(Id, Statuses.InProgress);

            // Szimuláció indítása
            simulation.getSimulation().start();

            // Feladatok kinyerése
            List<Cloudlet> finishedCloudlets = simulation.getBroker().getCloudletFinishedList();

            // Részletes feladat-eredmények (Cloudlet Execution Results)
            List<CloudletExecutionResultDto> resList = finishedCloudlets.stream().map(c ->
                    CloudletExecutionResultDto.builder()
                            .cloudletId(c.getId())
                            .status(c.getStatus().name())
                            .vmId(c.getVm() != null ? c.getVm().getId() : -1)
                            .execTime(c.getActualCpuTime())
                            .startTime(c.getExecStartTime())
                            .finishTime(c.getFinishTime())
                            .build()
            ).collect(Collectors.toList());

            // Dinamikus számítások
            // Dinamikus számítások
            double makespan = simulation.getSimulation().clock();

            // JAVÍTOTT KÖLTSÉGSZÁMÍTÁS
            double totalCost = finishedCloudlets.stream()
                    .mapToDouble(c -> {
                        // Csak akkor van költség, ha a feladat kapott VM-et és Hostot
                        if (c.getVm() != null && c.getVm().getHost() != null && c.getVm().getHost().getDatacenter() != null) {
                            // Lekérjük a Datacenter másodpercalapú árazását
                            double costPerSec = c.getVm().getHost().getDatacenter().getCharacteristics().getCostPerSecond();
                            // Költség = Tényleges CPU idő * Másodpercdíj
                            return c.getActualCpuTime() * costPerSec;
                        }
                        return 0.0;
                    })
                    .sum();

            int successCount = (int) finishedCloudlets.stream()
                    .filter(c -> c.getStatus() == Cloudlet.Status.SUCCESS)
                    .count();

            // Fő Eredmény objektum megépítése
            ResultDto resultDto = ResultDto.builder()
                    .jobId(Id)
                    .totalExecutionTime(makespan) // Nincs többé /1000 osztás!
                    .totalCpuCost(totalCost)      // Nincs többé hardcoded 15!
                    .totalCloudlets(simulation.getTotalCloudletCount())
                    .successfulCloudlets(successCount)
                    .cloudletResults(resList)
                    .build();

            cache.AddResult(Id, resultDto);
            cache.AddStatus(Id, Statuses.Completed);

        } catch (Exception e) {
            this.cache.AddStatus(Id, Statuses.Failed);
        }
    }
}
