package com.dilarakuloglu.realtime_vehicle_tracker.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TripCreationDto(

        @NotNull Long vehicleId,

        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double originLat,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double originLng,

        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double destLat,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double destLng,

        @NotNull @Positive Double speedKmh) {
}
