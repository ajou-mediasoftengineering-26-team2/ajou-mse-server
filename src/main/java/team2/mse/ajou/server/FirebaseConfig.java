// FirebaseConfig.java
package team2.mse.ajou.server;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;

/** Firebase Configure file
 *  Don't have to change it
 *
 * @author Junseo Hwang 202322128
 */

@Configuration
public class FirebaseConfig {
    @Autowired
    Environment env;

    @PostConstruct
    public void init() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            // Load file from the classpath
            // (-> This makes the application automatically find file inside the resources folder.)
            // Classpath 에서 파일 불러오기 (-> resources 폴더 안에서 파일을 알아서 잘 찾게 만듭니다.)

            // NOTE: distinguish between prod and dev server firebase configs!!
            FirebaseOptions options;
            ClassPathResource serviceAccountJson;

            if (env.matchesProfiles("dev")) {
                // use DEV firebase
                serviceAccountJson = new ClassPathResource("ajou-mse-firebase-dev.json");

                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccountJson.getInputStream()))
                        .setDatabaseUrl("https://ajou-mse-740f2-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .build();
            } else {
                // use NORMAL
                serviceAccountJson = new ClassPathResource("ajou-mse-firebase-adminsdk-fbsvc-1dee6ea80f.json");

                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccountJson.getInputStream()))
                        .setDatabaseUrl("https://ajou-mse-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .build();
            }

            FirebaseApp.initializeApp(options);
        }
    }

    // If you want to update firebase, use this(FirebaseDatabase)
    // 만약 firebase를 업데이트 하고 싶다면 이것(FirebaseDatabase)을 사용하면 됩니다
    @Bean
    public FirebaseDatabase firebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }
}