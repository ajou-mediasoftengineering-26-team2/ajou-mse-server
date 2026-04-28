// FirebaseConfig.java
package team2.mse.ajou.server;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            // Classpath 에서 파일 불러오기 (-> resources 폴더 안에서 파일을 알아서 잘 찾게 만듭니다.)
            ClassPathResource serviceAccountJson = new ClassPathResource("ajou-mse-firebase-adminsdk-fbsvc-1dee6ea80f.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountJson.getInputStream()))
                    .setDatabaseUrl("https://ajou-mse-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public FirebaseDatabase firebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }
}