package com.dilarakuloglu.realtime_vehicle_tracker.controller;

import com.dilarakuloglu.realtime_vehicle_tracker.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;

    @PostMapping("/spawn")
    public String spawn(@RequestParam int count) {
        simulationService.spawnVehicles(count);
        return count + " araç oluşturuldu";
    }
}
