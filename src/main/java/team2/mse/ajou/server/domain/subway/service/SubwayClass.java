package team2.mse.ajou.server.domain.subway.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.subway.model.realtimemetro.RealtimePositionList;
import team2.mse.ajou.server.domain.subway.model.realtimemetro.SubwayResponse;
import team2.mse.ajou.server.domain.subway.model.zonemap.StationZoneRule;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


//202322158 이준상
//About call Subway API Key
@Service
@RequiredArgsConstructor
public class SubwayClass {
    private final SubwayApiClient subwayApiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<StationZoneRule> stationZoneRules = new ArrayList<>();
    private String trackedTrainNo;

    //only once execute
    @PostConstruct
    void loadStationZoneRules() throws IOException {
        ClassPathResource resource = new ClassPathResource("subway/station-zone-rules.json");
        try (InputStream in = resource.getInputStream()) {
            List<StationZoneRule> loadedRules = objectMapper.readValue(in, new TypeReference<>() {});
            stationZoneRules.clear();
            stationZoneRules.addAll(loadedRules);
        }
    }

    // fixedRate: each of X time
    @Scheduled(fixedRate = 180000)
    public void trackStationPosition() {
        try {
            //call api
            SubwayResponse subway = subwayApiClient.fetch();

            //no data when call data
            if (subway == null || subway.realtimePositionList() == null || subway.realtimePositionList().isEmpty()) {
                trackedTrainNo = null;
                System.out.println("[3minute update] no train data");
                return;
            }


            List<RealtimePositionList> trains = subway.realtimePositionList();
            RealtimePositionList trackedTrain = findTrainByTrainNo(trains, trackedTrainNo);

            //if the tracked train is null
            if (trackedTrain == null) {
                trackedTrain = trains.get(0);
                trackedTrainNo = trackedTrain.trainNo();
                System.out.println("[3minute update] re-selected trainNo: " + trackedTrainNo);
            }

            String representativeStation = resolveRepresentativeStation(trackedTrain.statnId());

            //each of 3min
            System.out.println("[3minute update] " + trackedTrainNo
                    + " / statnId: " + trackedTrain.statnId()
                    + " / representative: " + representativeStation);
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    private RealtimePositionList findTrainByTrainNo(List<RealtimePositionList> trains, String targetTrainNo) {
        if (targetTrainNo == null || targetTrainNo.isBlank()) {
            return null;
        }

        for (RealtimePositionList train : trains) {
            if (targetTrainNo.equals(train.trainNo())) {
                return train;
            }
        }
        return null;
    }

    private String resolveRepresentativeStation(String statnId) {
        String key = extractLast4(statnId);
        if (key == null) {
            return "UNKNOWN";
        }

        int stationCode;
        try {
            stationCode = Integer.parseInt(key);
        } catch (NumberFormatException e) {
            return "UNKNOWN";
        }

        for (StationZoneRule rule : stationZoneRules) {
            if (stationCode >= rule.startId() && stationCode <= rule.endId()) {
                return rule.representativeStation();
            }
        }

        return "UNKNOWN";
    }

    private String extractLast4(String statnId) {
        if (statnId == null || statnId.length() < 4) {
            return null;
        }
        return statnId.substring(statnId.length() - 4);
    }
}
