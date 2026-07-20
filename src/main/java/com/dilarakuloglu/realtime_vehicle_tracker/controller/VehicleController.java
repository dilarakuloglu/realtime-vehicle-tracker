package com.dilarakuloglu.realtime_vehicle_tracker.controller;

import com.dilarakuloglu.realtime_vehicle_tracker.dto.VehicleCreationDto;
import com.dilarakuloglu.realtime_vehicle_tracker.entity.Vehicle;
import com.dilarakuloglu.realtime_vehicle_tracker.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{id}")
    public Vehicle getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    @PostMapping
    public Vehicle createVehicle(@RequestBody VehicleCreationDto dto) {
        return vehicleService.createVehicleAt(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }
}