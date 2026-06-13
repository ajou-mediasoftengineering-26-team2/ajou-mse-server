package team2.mse.ajou.server.domain.firebase.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.firebase.model.FrdbMatchData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

import java.util.UUID;

/**
 * FRDB (Firebase Realtime DB) Data modification handling service.
 *
 * @author Ahn Yubin / 202021088
 */
@Repository
public class FrdbRepository implements IFrdbRepository {
    private final DatabaseReference matchRootRef;
    private final FirebaseDatabase firebaseDatabase;

    public FrdbRepository(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;

        matchRootRef = firebaseDatabase
                .getReference("matches");
    }

    /**
     * Obtains a reference to match data with given ID. Used for sending updates to match data.
     *
     * @param id Match ID.
     * @return Database reference.
     */
    @Override
    public DatabaseReference getMatchRef(UUID id) {
        return matchRootRef
                .child(id.toString());
    }

    /**
     * Send match data updates for match with given ID.
     *
     * @param id        Match ID.
     * @param matchData New match data.
     */
    @Override
    public void setMatch(UUID id, MatchData matchData) {
        getMatchRef(id).setValueAsync(FrdbMatchData.from(matchData));
    }

    /**
     * Removes all match data. Mainly used for debugging.
     */
    @Override
    public void clearAllMatch() {
        matchRootRef.removeValueAsync();
    }
}
