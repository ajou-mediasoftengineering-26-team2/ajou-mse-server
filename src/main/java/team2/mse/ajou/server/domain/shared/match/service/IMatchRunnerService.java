package team2.mse.ajou.server.domain.shared.match.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 매치를 관리하며, 매치에서 사용하는 Observer 연결, 타이머 관리 등 로직을 실행하는 서비스.
 * Service for running logics for match.
 *
 * @author Ahn Yubin / 202021088
 */
public interface IMatchRunnerService {
    @Transactional
    UUID createNewMatch();

    @Transactional
    void deleteMatch(UUID matchId);

    @Transactional
    void deleteAllMatches();

    @Transactional
    UUID findOpenMatch();

    @Transactional
    boolean joinPlayerToMatch(UUID playerId, UUID matchId);

    @Transactional
    boolean leavePlayerFromMatch(UUID playerId, UUID matchId);
}
