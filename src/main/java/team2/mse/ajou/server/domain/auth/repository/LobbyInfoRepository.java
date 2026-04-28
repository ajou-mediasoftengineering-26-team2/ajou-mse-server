package team2.mse.ajou.server.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.auth.model.LobbyInfo;

import java.util.UUID;

@Repository
public interface LobbyInfoRepository extends JpaRepository<LobbyInfo, UUID> {
}
