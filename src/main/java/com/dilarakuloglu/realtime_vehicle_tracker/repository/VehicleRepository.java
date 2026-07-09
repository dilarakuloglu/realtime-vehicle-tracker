package com.dilarakuloglu.realtime_vehicle_tracker.repository;

import com.dilarakuloglu.realtime_vehicle_tracker.entity.Vehicle;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v WHERE v.status = :status")
    List<Vehicle> findVehiclesByStatus(@Param("status") VehicleStatus status);

}