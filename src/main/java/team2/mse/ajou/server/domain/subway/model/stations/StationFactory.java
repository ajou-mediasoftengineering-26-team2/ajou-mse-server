package team2.mse.ajou.server.domain.subway.model.stations;


/**
 * Subway Station Factory
 * @author Junseo Hwang 202322128
 */
public class StationFactory {
    /**
     * station string에 맞는 station 로직 구현체를 생성한다.
     * This creates an implementation of the station logic that matches the station name.
     * @param station station name
     * @return implementation of the station logic that matches the station name
     */
    public static IStation createStation(String station)
    {
        if(station == null) return null;
        return switch (station){
            case "HONGIK_UNIV" -> new StationHangikUniv();
            case "CITY_HALL" -> new StationCityHall();
            case "SEONGSU" -> new StationSeongSu();
            case "GANGNAM" -> new StationGangNam();
            case "SILLIM" -> new StationSillim();
            default -> null;
        };
    }
}
