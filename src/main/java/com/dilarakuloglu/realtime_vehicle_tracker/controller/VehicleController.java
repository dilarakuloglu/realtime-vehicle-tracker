package com.dilarakuloglu.realtime_vehicle_tracker.controller;

import com.dilarakuloglu.realtime_vehicle_tracker.dto.VehicleCreationDto;
import com.dilarakuloglu.realtime_vehicle_tracker.dto.VehicleResponseDto;
import com.dilarakuloglu.realtime_vehicle_tracker.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public List<VehicleResponseDto> getAllVehicles() {
        return vehicleService.getAllVehicles()
                .stream()
                .map(VehicleResponseDto::from)
                .toList();
    }

    @GetMapping("/{id}")
    public VehicleResponseDto getVehicleById(@PathVariable Long id) {
        return VehicleResponseDto.from(vehicleService.getVehicleById(id));
    }

    @PostMapping
    public VehicleResponseDto createVehicle(@RequestBody @Valid VehicleCreationDto dto) {
        return VehicleResponseDto.from(vehicleService.createVehicleAt(dto));
    }

    @DeleteMapping("/{id}")
    public void deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }
}
