package com.dilarakuloglu.realtime_vehicle_tracker.repository;

import com.dilarakuloglu.realtime_vehicle_tracker.entity.Trip;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT t FROM Trip t WHERE t.status = :status")
    List<Trip> findTripsByStatus(@Param("status") TripStatus status);
}