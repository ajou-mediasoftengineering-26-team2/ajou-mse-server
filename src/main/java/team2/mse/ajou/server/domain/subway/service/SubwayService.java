package team2.mse.ajou.server.domain.subway.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//202322158 이준상
@Service
public class SubwayService {
    //define FirebaseData
    private final FirebaseDatabase firebaseDatabase;

    @Autowired
    public SubwayService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    public void putResult(String station) throws Exception {
        // As an admin, the app has access to read and write all data, regardless of Security Rules
        DatabaseReference ref = firebaseDatabase.getReference();
        ref.child("testGame")
                .child("currentStation")
                .setValueAsync(station);

    }
}
