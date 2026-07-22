package com.dilarakuloglu.realtime_vehicle_tracker.service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dilarakuloglu.realtime_vehicle_tracker.dto.OsrmResponse;
import com.dilarakuloglu.realtime_vehicle_tracker.dto.TripCreationDto;
import com.dilarakuloglu.realtime_vehicle_tracker.entity.Trip;
import com.dilarakuloglu.realtime_vehicle_tracker.entity.Vehicle;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.TripStatus;
import com.dilarakuloglu.realtime_vehicle_tracker.enums.VehicleStatus;
import com.dilarakuloglu.realtime_vehicle_tracker.event.VehicleLocationEvent;
import com.dilarakuloglu.realtime_vehicle_tracker.kafka.VehicleEventProducer;
import com.dilarakuloglu.realtime_vehicle_tracker.repository.TripRepository;
import com.dilarakuloglu.realtime_vehicle_tracker.repository.VehicleRepository;
import com.dilarakuloglu.realtime_vehicle_tracker.util.GeoUtils;

import java.time.Duration;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.*;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
//public TripService(TripRepository tripRepository, VehicleRepository vehicleRepository) { ... }
// runtime da hata verir.
public class TripService {

    private final TripRepository tripRepository; // bağımlılık için final kullanıyoruz.
    private final VehicleRepository vehicleRepository;
    private final VehicleEventProducer vehicleEventProducer;
    private final RoutingService routingService;
    private final ObjectMapper objectMapper;

    public Trip getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found: " + id));
    }

    // Frontend'in rotayı çizmesi için: trip'in yol noktalarını [lat,lng] listesi olarak döndür
    public List<List<Double>> getRoutePoints(Long tripId) {
        Trip trip = getTripById(tripId);
        Double[][] route = parseRoute(trip.getRouteGeometryJson());
        if (route == null) {
            return List.of();
        }
        List<List<Double>> out = new ArrayList<>();
        for (Double[] p : route) {
            out.add(List.of(p[0], p[1]));
        }
        return out;
    }

    public List<Trip> getTripsByStatus(TripStatus status) {
        return tripRepository.findTripsByStatus(status);
    }
    // Simülasyon zamanını hızlandırma katsayısı: 1 gerçek sn = TIME_SCALE simülasyon sn
    private static final double TIME_SCALE = 200;
    

    public void processTripMovement(Trip trip ){
     Duration elapsed= Duration.between(trip.getStartTime(),LocalDateTime.now());
     double elapsedSeconds = elapsed.getSeconds() * TIME_SCALE;
     double progressPercent = elapsedSeconds / trip.getEstimatedDurationSeconds();

     
        double newLat;
        double newLng;
        VehicleStatus vehicleStatus;
        TripStatus tripStatus;

        if (progressPercent >= 1.0) {
            progressPercent = 1.0;
            newLat = trip.getDestLat();
            newLng = trip.getDestLng();
            vehicleStatus = VehicleStatus.ARRIVED;
            tripStatus = TripStatus.COMPLETED;
 
        } else {
            vehicleStatus = VehicleStatus.MOVING;
            tripStatus = TripStatus.IN_PROGRESS;

            Double[][] route = parseRoute(trip.getRouteGeometryJson());
            if (route != null && route.length >= 2) {
                // gerçek yol boyunca progressPercent'e denk gelen noktayı bul
                double[] pos = pointOnRoute(route, progressPercent);
                newLat = pos[0];
                newLng = pos[1];
            } else {
                // rota yoksa (OSRM patlamış) düz çizgi fallback
                newLat = trip.getOriginLat() + (trip.getDestLat() - trip.getOriginLat()) * progressPercent;
                newLng = trip.getOriginLng() + (trip.getDestLng() - trip.getOriginLng()) * progressPercent;
            }
        }

        VehicleLocationEvent event = new VehicleLocationEvent(
                trip.getVehicle().getId(),
                trip.getId(),
                newLat,
                newLng,
                progressPercent,
                vehicleStatus,
                tripStatus,
                LocalDateTime.now(),
                trip.getDestLat(),
                trip.getDestLng(),
                trip.getSpeedKmh()
        );

        vehicleEventProducer.publishLocationUpdate(event);



    }

    // routeGeometryJson (JSON) -> Double[][] (her satır [lat,lng]); yoksa/parse hatasında null
    private Double[][] parseRoute(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Double[][].class);
        } catch (Exception e) {
            return null;
        }
    }

    // Yol boyunca kümülatif mesafeye göre fraction (0..1) noktasını bul, [lat,lng] döndür
    private double[] pointOnRoute(Double[][] route, double fraction) {
        double total = 0;
        for (int i = 0; i < route.length - 1; i++) {
            total += GeoUtils.calculateDistance(route[i][0], route[i][1], route[i + 1][0], route[i + 1][1]);
        }
        double target = fraction * total;

        double acc = 0;
        for (int i = 0; i < route.length - 1; i++) {
            double seg = GeoUtils.calculateDistance(route[i][0], route[i][1], route[i + 1][0], route[i + 1][1]);
            if (acc + seg >= target) {
                double f = (seg == 0) ? 0 : (target - acc) / seg;
                double lat = route[i][0] + (route[i + 1][0] - route[i][0]) * f;
                double lng = route[i][1] + (route[i + 1][1] - route[i][1]) * f;
                return new double[]{lat, lng};
            }
            acc += seg;
        }
        // fraction ~1 → son nokta
        return new double[]{route[route.length - 1][0], route[route.length - 1][1]};
    }

    @Transactional
    public void terminateTrip(Long tripId){
        Trip trip = getTripById(tripId);
        if(trip.getStatus() != TripStatus.IN_PROGRESS){
        }
        trip.setStatus(TripStatus.COMPLETED);
        tripRepository.save(trip); // kafka
    }

    public List<Trip> getActiveTrips(){
        List <Trip>activeTrips = getTripsByStatus(TripStatus.IN_PROGRESS);
        return activeTrips;
    }

    public List<Trip> getAllTrips() {
    return tripRepository.findAll();
    }

    @Transactional
    public Trip createTrip(TripCreationDto dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + dto.id()));

        if (tripRepository.hasActiveTripForVehicle(vehicle.getId())) {
        throw new IllegalStateException("Vehicle already has an active trip: " + vehicle.getId());
        }

        Trip trip = new Trip();
        trip.setVehicle(vehicle);
        trip.setOriginLat(dto.originLat());
        trip.setOriginLng(dto.originLng());
        trip.setDestLat(dto.destLat());
        trip.setDestLng(dto.destLng());
        trip.setSpeedKmh(dto.speedKmh());
        trip.setStatus(TripStatus.IN_PROGRESS);

        double distanceKm;
        try {
            OsrmResponse osrm = routingService.getRoute(
                    dto.originLat(), dto.originLng(), dto.destLat(), dto.destLng());
            OsrmResponse.Route route = osrm.routes().get(0);

            // OSRM [lng,lat] verir → projeye uygun [lat,lng]'ye çevir
            List<List<Double>> latLng = new ArrayList<>();
            for (List<Double> p : route.geometry().coordinates()) {
                latLng.add(List.of(p.get(1), p.get(0))); // p.get(1)=lat, p.get(0)=lng
            }
            // listeyi JSON string'e çevirip Trip'e yaz
            trip.setRouteGeometryJson(objectMapper.writeValueAsString(latLng));
            // süreyi OSRM'in gerçek yol mesafesinden hesapla (metre → km)
            distanceKm = route.distance() / 1000.0;
        } catch (Exception e) {
            // OSRM patlarsa: rota yok, eski düz-çizgi mantığına düş
            trip.setRouteGeometryJson(null);
            distanceKm = GeoUtils.calculateDistance(
                    dto.originLat(), dto.originLng(), dto.destLat(), dto.destLng());
        }

        long durationSeconds = Math.round(distanceKm / dto.speedKmh() * 3600);
        trip.setStartTime(LocalDateTime.now());
        trip.setEstimatedDurationSeconds(durationSeconds);
        
        return tripRepository.save(trip); // kafka entegrasyonu 
    }

}
