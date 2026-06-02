package team2.mse.ajou.server.domain.shared.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;
import java.util.UUID;

/**
 * Player data DB Repository. Uses JPA's repository.
 *
 * @author Ahn Yubin / 202021088
 */
@Repository
public interface PlayerDataRepository extends JpaRepository<PlayerData, UUID> {
    /**
     * Fetch player by username.
     * @param username Username.
     * @return List of players with matching username. Usually has only one entry.
     */
    List<PlayerData> findByUsername(String username);

    /**
     * Checks whether given username is in use.
     * @param username Username.
     * @return Whether the username is in use.
     */
    boolean existsByUsername(String username);
}
