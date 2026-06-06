package team2.mse.ajou.server.domain.shared.match.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

/**
 * 상태별 매치의 로직을 실행하는 서비스.
 * Service for running logics for match.
 *
 * @author Ahn Yubin / 202021088
 */
@Service
public class MatchRunnerService {
    // 현재 관리중인 (i.e. 옵저버가 돌아가는) 매치들
    Map<UUID, MatchLogicData> allRunningMatches;

    public void addMatch(UUID matchId) {
        MatchLogicData data = new MatchLogicData(matchId);

        allRunningMatches.put(matchId, data);
    }

    public void removeMatch(UUID matchId) {
        allRunningMatches.remove(matchId);
    }

    private static class MatchLogicData {
        private ScheduledFuture<?> timerHandle;
        private UUID matchId;

        public MatchLogicData(UUID matchId) {
            this.timerHandle = null;
            this.matchId = matchId;
        }
    }
}