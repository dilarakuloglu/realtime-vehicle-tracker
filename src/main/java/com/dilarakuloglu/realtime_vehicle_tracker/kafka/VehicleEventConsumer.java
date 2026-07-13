package com.dilarakuloglu.realtime_vehicle_tracker.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.dilarakuloglu.realtime_vehicle_tracker.event.VehicleLocationEvent;

@Service
public class VehicleEventConsumer {

    @KafkaListener(id = "vehicle-tracker-group", topics = "vehicle-location-updates")
    // belirlenen topic e her mesaj geldiğinde otomatik çalışır.
    public void handleLocationEvent(VehicleLocationEvent locationEvent){
            System.out.println("alındı " + locationEvent); // VehicleLocationEvent record tipinde toString()
    }
    
}
