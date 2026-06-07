package team2.mse.ajou.server.domain.shared.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

import java.util.UUID;

/**
 * Match data DB Repository. Uses JPA's repository.
 *
 * @author Ahn Yubin / 202021088
 */
@Repository
public interface MatchDataJpaRepository extends JpaRepository<MatchData, UUID> {
}
