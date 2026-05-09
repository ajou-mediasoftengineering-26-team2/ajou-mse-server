package team2.mse.ajou.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class that allows browser clients (i.e. testing pages) to make an API request without getting blocked by CORS.
 * @author Ahn Yubin / 202021088
 */
@Configuration
public class WebCorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // both of those pages are either local port / deployed URL of testing pages. Latter can be accessed online.
                .allowedOrigins("http://localhost:6767", "https://ajou-mse-backend-page.zik-proffy.workers.dev")
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
