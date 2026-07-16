package com.dilarakuloglu.realtime_vehicle_tracker.kafka;

import com.dilarakuloglu.realtime_vehicle_tracker.event.VehicleLocationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleEventProducer {

    private final KafkaTemplate<String, VehicleLocationEvent> kafkaTemplate;

    private static final String TOPIC = "vehicle-location-tracker";

    public void publishLocationUpdate(VehicleLocationEvent event) {
        kafkaTemplate.send(TOPIC, event.vehicleId().toString(), event);
    }
}