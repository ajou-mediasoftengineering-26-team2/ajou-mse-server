package team2.mse.ajou.server.domain.subway.model;

public record ErrorMessage(
        long total,
        String code,
        String developerMessage,
        String link,
        String message,
        long status
) {}
