package team2.mse.ajou.server.domain.shared.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;
import java.util.UUID;

/**
 * 플레이어 정보 내부 DB Repository. JPA의 그것을 사용합니다.
 *
 * @author Ahn yubin / 202021088
 */
@Repository
public interface PlayerDataRepository extends JpaRepository<PlayerData, UUID> {
    /**
     * 사용자 이름으로 플레이어 정보 조회.
     * @param username 사용자 이름.
     * @return 해당 이름을 가진 플레이어 목록.
     */
    List<PlayerData> findByUsername(String username);

    /**
     * 사용자 이름으로 플레이어 존재 여부 조회.
     * @param username 사용자 이름.
     * @return 해당 이름이 사용 중인지 여부.
     */
    boolean existsByUsername(String username);
}
