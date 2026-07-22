package com.dilarakuloglu.realtime_vehicle_tracker.dto;

import com.dilarakuloglu.realtime_vehicle_tracker.entity.Vehicle;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.VehicleStatus;

public record VehicleResponseDto(
        Long id,
        String name,
        VehicleStatus status,
        Double currentLat,
        Double currentLng) {

    public static VehicleResponseDto from(Vehicle vehicle) {
        return new VehicleResponseDto(
                vehicle.getId(),
                vehicle.getName(),
                vehicle.getStatus(),
                vehicle.getCurrentLat(),
                vehicle.getCurrentLng());
    }
}
