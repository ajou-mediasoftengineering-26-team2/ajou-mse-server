package team2.mse.ajou.server.domain.firebase.service;

import com.google.firebase.database.DatabaseReference;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

import java.util.UUID;

/**
 * FRDB (Firebase Realtime DB) Data modification handling service.
 *
 * @author Ahn Yubin / 202021088
 */
public interface IFrdbRepository {
    DatabaseReference getMatchRef(UUID id);

    void setMatch(UUID id, MatchData matchData);

    void clearAllMatch();
}
