package com.dilarakuloglu.realtime_vehicle_tracker.entity;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.VehicleStatus;
import lombok.*;
import jakarta.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    

    @Enumerated (EnumType.STRING)
    private VehicleStatus status;

    private Double currentLat;
    private Double currentLng;




    
}
