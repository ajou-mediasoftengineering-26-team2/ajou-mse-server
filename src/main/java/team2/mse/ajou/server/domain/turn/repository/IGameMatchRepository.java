package team2.mse.ajou.server.domain.turn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.turn.model.GameMatch;

/**
 * @author Junseo Hwang
 */
@Repository
public interface IGameMatchRepository extends JpaRepository<GameMatch, String> {
}
