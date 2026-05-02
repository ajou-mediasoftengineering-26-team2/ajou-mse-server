package team2.mse.ajou.server.domain.shared.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

import java.util.UUID;

/**
 * 매치 정보 내부 DB Repository. JPA의 그것을 사용합니다.
 *
 * @author Ahn yubin / 202021088
 */
@Repository
public interface MatchDataRepository extends JpaRepository<MatchData, UUID> {
}
