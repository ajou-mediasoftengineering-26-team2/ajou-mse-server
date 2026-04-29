package team2.mse.ajou.server.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

import java.util.UUID;

@Repository
public interface MatchDataRepository extends JpaRepository<MatchData, UUID> {
}
