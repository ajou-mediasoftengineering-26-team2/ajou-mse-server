package team2.mse.ajou.server.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.shared.LobbyData;

import java.util.UUID;

@Repository
public interface LobbyDataRepository extends JpaRepository<LobbyData, UUID> {
}
