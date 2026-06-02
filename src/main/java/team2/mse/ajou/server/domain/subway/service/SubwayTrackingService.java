package team2.mse.ajou.server.domain.subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.subway.api.SubwayApiClient;
import team2.mse.ajou.server.domain.subway.model.realtimemetro.RealtimePositionList;
import team2.mse.ajou.server.domain.subway.model.realtimemetro.SubwayResponse;
import team2.mse.ajou.server.domain.subway.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;
//202322158 이준상
//This class is keeps track of the train number we first defined.
@Service
@RequiredArgsConstructor
public class SubwayTrackingService {
    private final SubwayApiClient subwayApiClient;
    private final StationZoneResolver stationZoneResolver;
    private final SubwayService subwayService;
    private String trackedTrainNo;

    private final StationRepository stationRepository;


    //This function identifies a specific inbound train from real-time API data, resolves its current location into a representative station name, and uploads the result to the subway service.
    public void trackAndUploadRepresentativeStation() throws Exception {
        SubwayResponse subway = subwayApiClient.fetch();

        if (subway == null || subway.realtimePositionList() == null || subway.realtimePositionList().isEmpty()) {
            trackedTrainNo = null;
            System.out.println("[subway] no train data");
            stationRepository.setStation(Util.UNKNOWN);
            return;
        }

        List<RealtimePositionList> inboundTrains = filterInboundTrains(subway.realtimePositionList());
        if (inboundTrains.isEmpty()) {
            trackedTrainNo = null;
            System.out.println("[subway] no updnLine=0 train data");
            stationRepository.setStation(Util.UNKNOWN);
            return;
        }

        RealtimePositionList trackedTrain = findTrainByTrainNo(inboundTrains, trackedTrainNo);
        if (trackedTrain == null) {
            trackedTrain = inboundTrains.get(0);
            trackedTrainNo = trackedTrain.trainNo();
            System.out.println("[subway] re-selected trainNo: " + trackedTrainNo);
        }

        String representativeStation = stationZoneResolver.resolveRepresentativeStation(trackedTrain.statnId());
        subwayService.putResult(representativeStation);
        stationRepository.setStation(representativeStation);
        System.out.println("[subway] trainNo: " + trackedTrainNo
                + " / statnId: " + trackedTrain.statnId()
                + " / representative: " + representativeStation);
    }

    //This function that checks if the train number We first defined is on that list.
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

    //only add 0(상행) train data to array data
    private List<RealtimePositionList> filterInboundTrains(List<RealtimePositionList> trains) {
        List<RealtimePositionList> inboundTrains = new ArrayList<>();
        for (RealtimePositionList train : trains) {
            if ("0".equals(train.updnLine())) {
                inboundTrains.add(train);
            }
        }
        return inboundTrains;
    }
}
