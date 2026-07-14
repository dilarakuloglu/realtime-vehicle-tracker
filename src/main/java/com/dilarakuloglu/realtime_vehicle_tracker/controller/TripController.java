package com.dilarakuloglu.realtime_vehicle_tracker.controller;
import com.dilarakuloglu.realtime_vehicle_tracker.dto.TripCreationDto;
import com.dilarakuloglu.realtime_vehicle_tracker.entity.Trip;
import com.dilarakuloglu.realtime_vehicle_tracker.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/{id}")
    public Trip getTripById(@PathVariable Long id) {
        return tripService.getTripById(id);
    }

    @GetMapping("/active")
    public List<Trip> getActiveTrips() {
        return tripService.getActiveTrips();
    }

    @PostMapping
    public Trip createTrip(@RequestBody TripCreationDto request) {
        return tripService.createTrip(
                request.vehicleId(),
                request.originLat(),
                request.originLng(),
                request.destLat(),
                request.destLng(),
                request.speedKmh()
        );
    }
}