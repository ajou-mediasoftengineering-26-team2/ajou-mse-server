package team2.mse.ajou.server.domain.subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.subway.model.RealtimePositionList;
import team2.mse.ajou.server.domain.subway.model.SubwayResponse;

import java.util.List;


//202322158 이준상
//About call Subway API Key
@Service
@RequiredArgsConstructor
public class SubwayClass {
    private final SubwayApiClient subwayApiClient;
    private String trackedTrainNo;

    // fixedRate: each of X time
    @Scheduled(fixedRate = 180000)
    public void trackStationPosition() {
        try {
            SubwayResponse subway = subwayApiClient.fetch();

            if (subway == null || subway.realtimePositionList() == null || subway.realtimePositionList().isEmpty()) {
                trackedTrainNo = null;
                System.out.println("[3minute update] no train data");
                return;
            }

            List<RealtimePositionList> trains = subway.realtimePositionList();
            RealtimePositionList trackedTrain = findTrainByTrainNo(trains, trackedTrainNo);

            if (trackedTrain == null) {
                trackedTrain = trains.get(0);
                trackedTrainNo = trackedTrain.trainNo();
                System.out.println("[3minute update] re-selected trainNo: " + trackedTrainNo);
            }

            System.out.println("[3minute update] " + trackedTrainNo + " / currentStation: " + trackedTrain.statnId());
        } catch (Exception e) {
            System.err.println("데이터 호출 중 에러 발생: " + e.getMessage());
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
}
