package com.dilarakuloglu.realtime_vehicle_tracker.event;

import com.dilarakuloglu.realtime_vehicle_tracker.enums.TripStatus;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.VehicleStatus;


import java.time.LocalDateTime;

public record VehicleLocationEvent(
        Long vehicleId,
        Long tripId,
        Double lat,
        Double lng,
        Double progressPercent,
        VehicleStatus vehicleStatus,
        TripStatus tripStatus,
        LocalDateTime timestamp,
        Double destLat,
        Double destLng

        

        // record otomatik olarak toString() üretir.
) {}