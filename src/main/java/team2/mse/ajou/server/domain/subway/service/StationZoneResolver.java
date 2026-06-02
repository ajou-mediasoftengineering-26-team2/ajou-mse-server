package team2.mse.ajou.server.domain.subway.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import team2.mse.ajou.server.domain.subway.Util;
import team2.mse.ajou.server.domain.subway.model.zonemap.StationZoneRule;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//202322158 이준상
//A class that fetches data from Line 2 station in a resource and compares it with data in a function parameter.
@Component
public class StationZoneResolver {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<StationZoneRule> stationZoneRules = new ArrayList<>();

    //This Function execute only once. Because of Annotation @PostConstruct
    @PostConstruct
    void loadStationZoneRules() throws IOException {
        //Get represent Data
        ClassPathResource resource = new ClassPathResource("subway/station-zone-rules.json");
        try (InputStream in = resource.getInputStream()) {
            List<StationZoneRule> loadedRules = objectMapper.readValue(in, new TypeReference<>() {});
            stationZoneRules.clear();
            stationZoneRules.addAll(loadedRules);
        }
    }

    //This function identifies and returns the representative station name by checking if a given station ID falls within specific numeric ranges defined in the zone rules.
    public String resolveRepresentativeStation(String statnId) {
        String key = extractLast4(statnId);
        if (key == null) {
            return Util.UNKNOWN;
        }

        int stationCode;
        try {
            stationCode = Integer.parseInt(key);
        } catch (NumberFormatException e) {
            return Util.UNKNOWN;
        }

        for (StationZoneRule rule : stationZoneRules) {
            if (stationCode >= rule.startId() && stationCode <= rule.endId()) {
                return rule.representativeStation();
            }
        }

        return Util.UNKNOWN;
    }

    //return Last 4 String
    private String extractLast4(String statnId) {
        if (statnId == null || statnId.length() < 4) {
            return null;
        }
        return statnId.substring(statnId.length() - 4);
    }
}
