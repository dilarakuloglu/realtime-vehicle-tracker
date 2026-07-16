package com.dilarakuloglu.realtime_vehicle_tracker.kafka;

import com.dilarakuloglu.realtime_vehicle_tracker.entity.Trip;
import com.dilarakuloglu.realtime_vehicle_tracker.entity.Vehicle;
import com.dilarakuloglu.realtime_vehicle_tracker.event.VehicleLocationEvent;
import com.dilarakuloglu.realtime_vehicle_tracker.repository.TripRepository;
import com.dilarakuloglu.realtime_vehicle_tracker.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleEventConsumer {

    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;

    @KafkaListener(id = "vehicle-tracker-group", topics = "vehicle-location-tracker")
    @Transactional
    public void handleLocationEvent(VehicleLocationEvent event) {
        Vehicle vehicle = vehicleRepository.findById(event.vehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + event.vehicleId()));
        vehicle.setCurrentLat(event.lat());
        vehicle.setCurrentLng(event.lng());
        vehicle.setStatus(event.vehicleStatus());
        vehicleRepository.save(vehicle); 

        Trip trip = tripRepository.findById(event.tripId())
                .orElseThrow(() -> new EntityNotFoundException("Trip not found: " + event.tripId()));
        trip.setStatus(event.tripStatus());
        tripRepository.save(trip);
        // repositorye kayıt artık kafkaya ait 
    }
}