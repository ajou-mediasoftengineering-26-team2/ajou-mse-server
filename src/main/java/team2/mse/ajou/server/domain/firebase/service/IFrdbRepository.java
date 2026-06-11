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
    /**
     * Obtains a reference to match data with given ID. Used for sending updates to match data.
     * @param id Match ID.
     * @return Database reference.
     */
    DatabaseReference getMatchRef(UUID id);

    /**
     * Send match data updates for match with given ID.
     * @param id Match ID.
     * @param matchData New match data.
     */
    void setMatch(UUID id, MatchData matchData);

    /**
     * Removes all match data. Mainly used for debugging.
     */
    void clearAllMatch();
}
