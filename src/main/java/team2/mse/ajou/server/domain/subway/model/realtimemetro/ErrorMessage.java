package team2.mse.ajou.server.domain.subway.model.realtimemetro;
//202322158 이준상
public record ErrorMessage(
        long total,
        String code,
        String developerMessage,
        String link,
        String message,
        long status
) {}
