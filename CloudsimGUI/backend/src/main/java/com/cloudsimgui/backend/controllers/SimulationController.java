package com.cloudsimgui.backend.controllers;

import com.cloudsimgui.backend.dto.ResultDto;
import com.cloudsimgui.backend.dto.SimulationContext;
import com.cloudsimgui.backend.dto.SimulationRequestDto;
import com.cloudsimgui.backend.enums.Statuses;
import com.cloudsimgui.backend.services.CacheService;
import com.cloudsimgui.backend.services.SimulationFactoryService;
import com.cloudsimgui.backend.services.SimulationRunnerService;
import org.cloudsimplus.core.CloudSimPlus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
public class SimulationController {

    private CacheService cache;
    private SimulationRunnerService runnerService;
    private SimulationFactoryService factory;

    public SimulationController(CacheService cache,SimulationRunnerService runner,SimulationFactoryService factory) {
        this.cache = cache;
        this.runnerService = runner;
        this.factory=factory;
    }

    @PostMapping("/start")
    public ResponseEntity<String> Start(@RequestBody SimulationRequestDto request){
        String id = UUID.randomUUID().toString();
        this.cache.AddStatus(id,Statuses.Pending);

        SimulationContext simulation = this.factory.GetSimulation(request);
        this.runnerService.RunSimulation(simulation, id);

        return ResponseEntity.ok(id);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<String> GetStatus(@PathVariable String id)
    {
        return  ResponseEntity.ok(cache.GetStatus(id).toString());
    }

    @GetMapping("/result/{id}")
    public ResponseEntity<ResultDto> GetResult(@PathVariable String id){
        return ResponseEntity.ok(this.cache.GetResult(id));
    }

}
