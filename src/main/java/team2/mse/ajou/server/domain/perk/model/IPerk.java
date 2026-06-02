package team2.mse.ajou.server.domain.perk.model;

public interface IPerk {

    void usePerkIfPossible();
    /**
     * 아이템이 사용 조건을 충족했는지 판단하는 함수
     * @return
     */
    boolean isAvailable();
}
