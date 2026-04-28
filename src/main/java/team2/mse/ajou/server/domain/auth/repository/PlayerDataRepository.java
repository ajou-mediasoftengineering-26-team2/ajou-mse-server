package team2.mse.ajou.server.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.shared.PlayerData;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerDataRepository extends JpaRepository<PlayerData, UUID> {
    List<PlayerData> findByUsername(String username);
    boolean existsByUsername(String username);
}
