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
 * Automatically wraps returning DTO / data class returned from `@RestController` into `ApiResponse<>` class.
 *
 * @author Ahn Yubin / 202021088
 */
@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        boolean isWrappingNeeded = !(returnType.getParameterType().equals(ApiResponse.class) || returnType.getParameterType().equals(ResponseEntity.class));

        if (isWrappingNeeded && converterType.isAssignableFrom(StringHttpMessageConverter.class)) {
            //System.err.println("[!!!] Controller 클래스에서 String을 그대로 반환하는겁니까..? 큰일납니다. 별도의 Response 클래스를 정의해서 사용해주시길 권장드려요...");
            System.err.println("[!!!] IT IS NOT RECOMMENDED TO RETURN STRING DIRECTLY FROM @RestConctroller. DEFINING A DEDICATED RESPONSE CLASS IS RECOMMENDED!");
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
