package team2.mse.ajou.server.domain.subway.repository;

import org.springframework.stereotype.Repository;

@Repository
public class StationRepository implements IStationRepository{

    private String currentStation;
    @Override
    public String getStation() {
        return currentStation;
    }

    @Override
    public void setStation(String station) {
        this.currentStation = station;
    }
}
