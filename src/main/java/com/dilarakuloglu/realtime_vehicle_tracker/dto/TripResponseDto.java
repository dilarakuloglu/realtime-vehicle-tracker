package com.dilarakuloglu.realtime_vehicle_tracker.dto;

import java.time.LocalDateTime;

import com.dilarakuloglu.realtime_vehicle_tracker.entity.Trip;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.TripStatus;

public record TripResponseDto(
        Long id,
        Long vehicleId,
        TripStatus status,
        LocalDateTime startTime,
        Long estimatedDurationSeconds,
        Double originLat,
        Double originLng,
        Double destLat,
        Double destLng,
        Double speedKmh) {

    public static TripResponseDto from(Trip trip) {
        return new TripResponseDto(
                trip.getId(),
                trip.getVehicle().getId(),
                trip.getStatus(),
                trip.getStartTime(),
                trip.getEstimatedDurationSeconds(),
                trip.getOriginLat(),
                trip.getOriginLng(),
                trip.getDestLat(),
                trip.getDestLng(),
                trip.getSpeedKmh());
    }
}
