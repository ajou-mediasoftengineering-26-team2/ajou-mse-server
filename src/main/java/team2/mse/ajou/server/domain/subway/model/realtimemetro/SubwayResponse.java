package team2.mse.ajou.server.domain.subway.model.realtimemetro;

import java.util.List;
//202322158 이준상

public record SubwayResponse(
        List<RealtimePositionList> realtimePositionList,
        ErrorMessage errorMessage
) {}

