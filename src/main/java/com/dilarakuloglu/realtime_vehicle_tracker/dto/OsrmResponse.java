package com.dilarakuloglu.realtime_vehicle_tracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OsrmResponse(
        String code,
        List<Route> routes) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Route(
            Double distance,          // metre
            Double duration,          // saniye
            Geometry geometry) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geometry(
            List<List<Double>> coordinates) {  // her eleman: [lng, lat]
        // her bir noktanın tutulacağı yer.
    }
}
