package team2.mse.ajou.server;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class TurnService {
    private final Firestore firestore;

    public TurnService(Firestore firestore) {
        this.firestore = firestore;
    }

    public void putResult(String id, String choice) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("choice", choice);

        ApiFuture<DocumentReference> future = firestore.collection("choices").add(data);
        future.get();
    }
}
