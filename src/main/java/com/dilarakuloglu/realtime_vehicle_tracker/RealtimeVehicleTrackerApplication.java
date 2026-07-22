package com.dilarakuloglu.realtime_vehicle_tracker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // zamanlanmış görevleri tanımlar
public class RealtimeVehicleTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealtimeVehicleTrackerApplication.class, args);
	}
}
