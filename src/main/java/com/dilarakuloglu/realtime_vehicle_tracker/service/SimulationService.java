package com.dilarakuloglu.realtime_vehicle_tracker.service;

import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dilarakuloglu.realtime_vehicle_tracker.dto.TripCreationDto;
import com.dilarakuloglu.realtime_vehicle_tracker.dto.VehicleCreationDto;
import com.dilarakuloglu.realtime_vehicle_tracker.entity.Trip;
import com.dilarakuloglu.realtime_vehicle_tracker.entity.Vehicle;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.VehicleStatus;
import com.dilarakuloglu.realtime_vehicle_tracker.repository.VehicleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class SimulationService {

    private final TripService tripService;
    private final VehicleService vehicleService;
    private final VehicleRepository vehicleRepository;

    private static final double MIN_LAT = 36.0;
    private static final double MAX_LAT = 42.0;
    private static final double MIN_LNG = 26.0;
    private static final double MAX_LNG = 45.0;
    private static final double MIN_SPEED = 30;
    private static final double MAX_SPEED = 120 ;

    // aktif vehicleları çekip simüle edecek.
    @Scheduled(fixedRate=2000)
    public void simulateTrips(){
        List <Trip> activeTrips = tripService.getActiveTrips();

        for (Trip t : activeTrips){
            tripService.processTripMovement(t);
        }
    }
    public Double assignRandomLat(){
        return MIN_LAT + Math.random()* (MAX_LAT-MIN_LAT);
    }
    public Double assignRandomLng(){
        return MIN_LNG + Math.random()* (MAX_LNG-MIN_LNG);
    }
    // arayüzde sayı alıp createVehicle tetikleyecek ?

    public void spawnVehicles(int count){
        for (int i= 0 ; i<count;i++ ){
        Vehicle vehicle = vehicleService.createVehicleAt(new VehicleCreationDto
            ("vehicle-" + UUID.randomUUID(),assignRandomLat(),assignRandomLng()));
            // ...
        }
    }
    // IDLE olan arabalara trip atama
    @Scheduled(fixedRate= 10000)
    public void spawnTrips(){
        List <Vehicle> active_vehicles = vehicleRepository.findVehiclesByStatus(VehicleStatus.IDLE);
        for(Vehicle v:active_vehicles ){
        TripCreationDto dto= new TripCreationDto(
            v.getId(),
            v.getCurrentLat(),
            v.getCurrentLng(),
            assignRandomLat(),
            assignRandomLng(),
            assignRandomSpeed());
        
        tripService.createTrip(dto);
        v.setStatus(VehicleStatus.MOVING);
        
    }
    }
    public double assignRandomSpeed (){
        return MIN_SPEED + Math.random()*(MAX_SPEED-MIN_SPEED);
    }


    
}
