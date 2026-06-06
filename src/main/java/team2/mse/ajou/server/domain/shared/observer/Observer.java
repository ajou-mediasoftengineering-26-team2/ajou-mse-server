package team2.mse.ajou.server.domain.shared.observer;

/**
 * `Observable` 로부터 변화를 구독하는 소비자 클래스.
 * </p>
 * Consumer class that subscribes changes from `Observable`.
 * 
 * @author Ahn Yubin / 202021088
 */
public interface Observer<T> {
    void onNotify(T newValue);
}
