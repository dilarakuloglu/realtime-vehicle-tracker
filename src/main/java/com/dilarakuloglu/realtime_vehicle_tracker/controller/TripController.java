package com.dilarakuloglu.realtime_vehicle_tracker.controller;
import com.dilarakuloglu.realtime_vehicle_tracker.dto.TripCreationDto;
import com.dilarakuloglu.realtime_vehicle_tracker.dto.TripResponseDto;
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
    public List<TripResponseDto> getAllTrips() {
        return tripService.getAllTrips()
                .stream()
                .map(TripResponseDto::from)
                .toList();
    }

    @GetMapping("/{id}")
    public TripResponseDto getTripById(@PathVariable Long id) {
        return TripResponseDto.from(tripService.getTripById(id));
    }

    @GetMapping("/active")
    public List<TripResponseDto> getActiveTrips() {
        return tripService.getActiveTrips()
                .stream()
                .map(TripResponseDto::from)
                .toList();
    }

    // Bir trip'in yol noktaları [lat,lng] — frontend rotayı çizmek için kullanır
    @GetMapping("/{id}/route")
    public List<List<Double>> getTripRoute(@PathVariable Long id) {
        return tripService.getRoutePoints(id);
    }

    @PostMapping
    public TripResponseDto createTrip(@RequestBody TripCreationDto request) {
        return TripResponseDto.from(tripService.createTrip(request));
    }
}