package team2.mse.ajou.server.domain.subway.repository;

import org.springframework.stereotype.Repository;
//202322158 이준상

//Create a repository for others to use reverse information easily
@Repository
public class StationRepository implements IStationRepository{

    private String currentStation;
    @Override
    public String getStation() {
        return currentStation;
    }

    @Override
    public void setStation(String station) {
        System.out.println("current Station" + station);
        this.currentStation = station;
    }
}
