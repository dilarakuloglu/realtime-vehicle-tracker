package com.dilarakuloglu.realtime_vehicle_tracker.dto;
import jakarta.validation.constraints.NotBlank;

public record VehicleCreationDto(
        @NotBlank String name
 ) {}
