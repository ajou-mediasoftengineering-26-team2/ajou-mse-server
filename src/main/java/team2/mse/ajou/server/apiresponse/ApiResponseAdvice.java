package team2.mse.ajou.server.apiresponse;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import team2.mse.ajou.server.apiresponse.model.ApiResponse;

/**
 * `@RestController`에서 반환하는 DTO 클래스를 이쁘게 `ApiResponse<>` 클래스로 감싸줍니다.
 *
 * @author yubin
 */
@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        boolean isSpringdocController = returnType.getContainingClass().getPackageName().startsWith("org.springdoc");
        if (isSpringdocController) {
            return false;
        }

        boolean isWrappingNeeded = !(returnType.getParameterType().equals(ApiResponse.class) || returnType.getParameterType().equals(ResponseEntity.class));

        if (isWrappingNeeded && converterType.isAssignableFrom(StringHttpMessageConverter.class)) {
            System.err.println("[!!!] Controller 클래스에서 String을 그대로 반환하는겁니까..? 큰일납니다. 별도의 Response 클래스를 정의해서 사용해주시길 권장드려요...");
        }

        return isWrappingNeeded && !converterType.isAssignableFrom(StringHttpMessageConverter.class);
    }

    @Override
    public @Nullable Object beforeBodyWrite(
            @Nullable Object responseBody,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response
    ) {
        return ApiResponse.ok(responseBody);
    }
}
