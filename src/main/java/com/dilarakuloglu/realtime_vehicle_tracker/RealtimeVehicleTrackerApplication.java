package com.dilarakuloglu.realtime_vehicle_tracker;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.dilarakuloglu.realtime_vehicle_tracker.enums.VehicleStatus;
import com.dilarakuloglu.realtime_vehicle_tracker.event.VehicleLocationEvent;
import com.dilarakuloglu.realtime_vehicle_tracker.kafka.VehicleEventProducer;

@SpringBootApplication
@EnableScheduling // zamanlanmış görevleri tanımlar
public class RealtimeVehicleTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealtimeVehicleTrackerApplication.class, args);
	}
}
