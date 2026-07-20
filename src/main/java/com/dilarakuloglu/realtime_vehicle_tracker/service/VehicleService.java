package com.dilarakuloglu.realtime_vehicle_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dilarakuloglu.realtime_vehicle_tracker.dto.VehicleCreationDto;
import com.dilarakuloglu.realtime_vehicle_tracker.entity.Vehicle;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.VehicleStatus;
import com.dilarakuloglu.realtime_vehicle_tracker.repository.VehicleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // final veya @NonNUll olan alanlar için lombok constructor parametresi üretir.
public class VehicleService {

    private final VehicleRepository vehicleRepository; // final olmazsa null pointer exception

    public Vehicle getVehicleById(Long id){
     return vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + id));
    }
    public List <Vehicle> getAllVehicles(){
        return vehicleRepository.findAll();
        
    }
    public Vehicle createVehicleAt(VehicleCreationDto dto){
        Vehicle vehicle = new Vehicle();
        vehicle.setName(dto.name());
        vehicle.setStatus(VehicleStatus.IDLE);
        vehicle.setCurrentLat(dto.currentLat());
        vehicle.setCurrentLng(dto.currentLng());
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id ){
        if(!vehicleRepository.existsById(id)){
            throw new EntityNotFoundException ("vehicle not found");
        }
        vehicleRepository.deleteById(id);
    }
    

    



    
}
