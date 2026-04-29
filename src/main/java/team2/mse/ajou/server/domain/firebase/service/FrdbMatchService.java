package team2.mse.ajou.server.domain.firebase.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.firebase.model.FrdbMatchData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

import java.util.UUID;

/**
 * FRDB 로비 수정 서비스
 *
 * @author yubin
 */
@Service
public class FrdbMatchService {
    private final DatabaseReference matchRootRef;
    private final FirebaseDatabase firebaseDatabase;

    public FrdbMatchService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;

        matchRootRef = firebaseDatabase
                .getReference("lobbies");
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
