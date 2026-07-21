package com.dilarakuloglu.realtime_vehicle_tracker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import com.dilarakuloglu.realtime_vehicle_tracker.dto.OsrmResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoutingService {

    private final RestClient osrmClient;   // bean adıyla eşleşiyor

    public OsrmResponse getRoute(double oLat, double oLng, double dLat, double dLng) {
        //OSRM lng,lat sırası ister
        String coords = oLng + "," + oLat + ";" + dLng + "," + dLat;

        return osrmClient.get()
                .uri("/route/v1/driving/{coords}?overview=full&geometries=geojson", coords)
                .retrieve()
                .body(OsrmResponse.class);
    }
}
