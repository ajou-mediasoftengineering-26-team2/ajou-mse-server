package team2.mse.ajou.server.domain.subway.api;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.subway.service.SubwayTrackingService;


//202322158 이준상
//About call
@Service
@RequiredArgsConstructor
public class SubwayClass {
    private final SubwayTrackingService subwayTrackingService;

    // fixedRate: each of X time this function execute.
    @Scheduled(fixedRate = 180000)
    public void trackStationPosition() {
        try {
            subwayTrackingService.trackAndUploadRepresentativeStation();
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }
}
