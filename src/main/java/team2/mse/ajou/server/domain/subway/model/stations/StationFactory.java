package team2.mse.ajou.server.domain.subway.model.stations;


/**
 * @author Junseo Hwang 202322128
 */
public class StationFactory {
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
