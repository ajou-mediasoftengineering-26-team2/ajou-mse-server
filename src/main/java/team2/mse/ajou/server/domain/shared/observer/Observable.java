package team2.mse.ajou.server.domain.shared.observer;

/**
 * 변화를 구독 (subscribe/observe) 가능한 subject.
 * Subject that can be observed/subscribed.
 *
 * @author Ahn Yubin / 202021088
 */
public interface Observable<T> {
    void addObserver(Observer<T> observer);
    void removeObserver(Observer<T> observer);
    void removeAllObservers();
    void notifyAllObservers();

    void updateValue(T newValue);
    T getValue();
}

