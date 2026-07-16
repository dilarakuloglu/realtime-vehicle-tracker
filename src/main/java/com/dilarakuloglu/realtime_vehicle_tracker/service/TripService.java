package com.dilarakuloglu.realtime_vehicle_tracker.service;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dilarakuloglu.realtime_vehicle_tracker.dto.TripCreationDto;
import com.dilarakuloglu.realtime_vehicle_tracker.entity.Trip;
import com.dilarakuloglu.realtime_vehicle_tracker.entity.Vehicle;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.TripStatus;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.VehicleStatus;
import com.dilarakuloglu.realtime_vehicle_tracker.event.VehicleLocationEvent;
import com.dilarakuloglu.realtime_vehicle_tracker.kafka.VehicleEventProducer;
import com.dilarakuloglu.realtime_vehicle_tracker.repository.TripRepository;
import com.dilarakuloglu.realtime_vehicle_tracker.repository.VehicleRepository;
import com.dilarakuloglu.realtime_vehicle_tracker.util.GeoUtils;

import java.time.Duration;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.*;

@Service
@RequiredArgsConstructor
//public TripService(TripRepository tripRepository, VehicleRepository vehicleRepository) { ... }
// runtime da hata verir.
public class TripService {

    private final TripRepository tripRepository; // bağımlılık için final kullanıyoruz.
    private final VehicleRepository vehicleRepository;
    private final VehicleEventProducer vehicleEventProducer;

    public Trip getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found: " + id));
    }

    public List<Trip> getTripsByStatus(TripStatus status) {
        return tripRepository.findTripsByStatus(status);
    }
    public void processTripMovement(Trip trip ){
     Duration elapsed= Duration.between(trip.getStartTime(),LocalDateTime.now());
     Long elapsedSeconds=elapsed.getSeconds();
     double progressPercent = (double) elapsedSeconds / trip.getEstimatedDurationSeconds();

     
        double newLat;
        double newLng;
        VehicleStatus vehicleStatus;
        TripStatus tripStatus;

        if (progressPercent >= 1.0) {
            progressPercent = 1.0;
            newLat = trip.getDestLat();
            newLng = trip.getDestLng();
            vehicleStatus = VehicleStatus.ARRIVED;
            tripStatus = TripStatus.COMPLETED;
        } else {
            newLat = trip.getOriginLat() + (trip.getDestLat() - trip.getOriginLat()) * progressPercent;
            newLng = trip.getOriginLng() + (trip.getDestLng() - trip.getOriginLng()) * progressPercent;
            vehicleStatus = VehicleStatus.MOVING;
            tripStatus = TripStatus.IN_PROGRESS;
        }

        VehicleLocationEvent event = new VehicleLocationEvent(
                trip.getVehicle().getId(),
                trip.getId(),
                newLat,
                newLng,
                progressPercent,
                vehicleStatus,
                tripStatus,
                LocalDateTime.now()
        );

        vehicleEventProducer.publishLocationUpdate(event); 



    }
    @Transactional
    public void terminateTrip(Long tripId){
        Trip trip = getTripById(tripId);
        if(trip.getStatus() != TripStatus.IN_PROGRESS){
        }
        trip.setStatus(TripStatus.COMPLETED);
        tripRepository.save(trip); // kafka
    }

    public List<Trip> getActiveTrips(){
        List <Trip>activeTrips = getTripsByStatus(TripStatus.IN_PROGRESS);
        return activeTrips;
    }
    
    public Long estimateDurationSeconds(Long tripId ){
        Trip trip = getTripById(tripId);
        double distanceKm= GeoUtils.calculateDistance(trip.getOriginLat(),trip.getOriginLng(),trip.getDestLat(),trip.getDestLng());
        double hours = distanceKm / trip.getSpeedKmh();
        return Math.round(hours*3600);
    }

    public List<Trip> getAllTrips() {
    return tripRepository.findAll();
    }

     /* 
    public void startTrip(Long tripId){
        Trip trip = getTripById(tripId);
        if(trip.getStatus() != TripStatus.IN_PROGRESS){
            throw new IllegalStateException();
        }
        trip.setStatus(TripStatus.IN_PROGRESS);
        tripRepository.save(trip);
        // test
        System.out.print("start time :" + trip.getStatus());
        }
        */

    @Transactional
    public Trip createTrip(TripCreationDto dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + dto.id()));
        Trip trip = new Trip();
        trip.setVehicle(vehicle);
        trip.setOriginLat(dto.originLat());
        trip.setOriginLng(dto.originLng());
        trip.setDestLat(dto.destLat());
        trip.setDestLng(dto.destLng());
        trip.setSpeedKmh(dto.speedKmh());
        trip.setStatus(TripStatus.IN_PROGRESS);
        trip.setStartTime(LocalDateTime.now());

        double distanceKm = GeoUtils.calculateDistance( dto.originLat(), dto.originLng(), dto.destLat(), dto.destLng());
        long durationSeconds = Math.round(distanceKm / dto.speedKmh() * 3600);
        trip.setStartTime(LocalDateTime.now());
        trip.setEstimatedDurationSeconds(durationSeconds);
        
        // test
        System.out.print(trip.getStatus());
        return tripRepository.save(trip); // kafka entegrasyonu 
    }

}
