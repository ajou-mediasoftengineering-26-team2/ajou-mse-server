package team2.mse.ajou.server.domain.subway.model;

public record RealtimePositionList(
        String recptnDt,
        String updnLine,
        String statnTid,
        String lstcarAt,
        String trainNo,
        long totalCount,
        String statnTnm,
        long selectedCount,
        String statnNm,
        String directAt,
        long rowNum,
        String trainSttus,
        String statnId,
        String subwayNm,
        String subwayId,
        String lastRecptnDt
) {}
