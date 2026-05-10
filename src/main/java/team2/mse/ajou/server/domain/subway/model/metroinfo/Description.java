package team2.mse.ajou.server.domain.subway.model.metroinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
//202322158 이준상

//DTO
public record Description(
        String lot,
        String route,
        String bldn_nm,
        String bldn_id,
        String lat
) {}