package team2.mse.ajou.server.domain.subway.model.zonemap;

//202322158 이준상

//DTO
public record StationZoneRule(
        int startId,
        int endId,
        String representativeStation
) {}
