package com.dilarakuloglu.realtime_vehicle_tracker.entity;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.TripStatus;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startTime;
    private Long estimatedDurationSeconds;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    // history table 

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    private Double originLat;
    private Double destLat;
    private Double originLng;
    private Double destLng;



    
}
