package team2.mse.ajou.server.domain.subway.model.metroinfo;

import java.util.List;
//202322158 이준상
public record MetroStation(
    Description description,
    List<Datum> data
) {}