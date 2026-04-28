package team2.mse.ajou.server.domain.subway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.subway.model.SubwayResponse;

//202322158 이준상
@Component
public class SubwayApiClient {
    private final RestClient restClient;

    @Value("${external.api.key}")
    private String apiKey;

    public SubwayApiClient() {
        restClient = RestClient.builder().baseUrl("http://swopenapi.seoul.go.kr/").build();
    }

    public SubwayResponse fetch() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ApiError(1001, "Not Define API KEY", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SubwayResponse response;
        try {
            response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/subway/{key}/json/realtimePosition/{start}/{end}/{line}")
                            .build(apiKey, "1", "5", "2호선"))
                    .retrieve()
                    .body(SubwayResponse.class);
        } catch (RestClientException err) {
            throw new ApiError(1002, "subway api response fail " + err.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        if (response == null) {
            throw new ApiError(1003, "", HttpStatus.BAD_GATEWAY);
        }
        return response;
    }
}
