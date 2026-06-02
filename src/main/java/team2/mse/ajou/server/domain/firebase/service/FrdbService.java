package team2.mse.ajou.server.domain.firebase.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.firebase.model.FrdbMatchData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

import java.util.UUID;

/**
 * FRDB (Firebase Realtime DB) Data modification handling service.
 *
 * @author Ahn Yubin / 202021088
 */
// TODO: Turn this into repository as we might not need more operations than what a repository does.
@Service
public class FrdbService {
    private final DatabaseReference matchRootRef;
    private final FirebaseDatabase firebaseDatabase;

    public FrdbService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;

        matchRootRef = firebaseDatabase
                .getReference("matches");
    }

    public DatabaseReference getMatchRef(UUID id) {
        return matchRootRef
                .child(id.toString());
    }

    public void setMatch(UUID id, MatchData matchData) {
        getMatchRef(id).setValueAsync(FrdbMatchData.from(matchData));
    }

    public void clearAllMatch() {
        matchRootRef.removeValueAsync();
    }
}
