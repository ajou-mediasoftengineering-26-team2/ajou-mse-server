package team2.mse.ajou.server.domain.perk.model;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

public interface IPerk {

    void usePerkIfPossible(MatchData matchData);
    /**
     * 아이템이 사용 조건을 충족했는지 판단하는 함수
     * @return
     */
    boolean isAvailable(MatchData matchData);
}
