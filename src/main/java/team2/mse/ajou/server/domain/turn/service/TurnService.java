package team2.mse.ajou.server.domain.turn.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TurnService {
    private final FirebaseDatabase firebaseDatabase;

    public TurnService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    public void putResult(String id, String choice) throws Exception {
        // As an admin, the app has access to read and write all data, regardless of Security Rules
        DatabaseReference ref = firebaseDatabase.getReference();
        ref.child(id)
           .child("choice")
           .setValueAsync(choice);

    }
}
