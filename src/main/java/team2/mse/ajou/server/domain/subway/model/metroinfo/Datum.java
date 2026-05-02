package team2.mse.ajou.server.domain.subway.model.metroinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
//202322158 이준상
public record Datum(
        String lot,
        Route route,
        String bldn_id,
        String bldn_nm,
        String lat
) {}