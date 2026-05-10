package team2.mse.ajou.server.domain.subway.model.metroinfo;

import com.fasterxml.jackson.annotation.JsonValue;
import java.io.IOException;

//202322158 이준상

//DTO
public enum Route {
    THE_2("2호선");

    private final String value;

    Route(String value) {
        this.value = value;
    }

    @JsonValue // 직렬화 시 "2호선"으로 변환
    public String toValue() {
        return value;
    }

    public static Route forValue(String value) throws IOException {
        if (value.equals("2호선")) return THE_2;
        throw new IOException("Cannot deserialize Route: " + value);
    }
}