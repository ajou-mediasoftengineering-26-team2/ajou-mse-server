package team2.mse.ajou.server.domain.shared.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer: Default observerable
 * @param <T>
 */
public class DefaultObservable<T> implements Observable<T> {
    private final List<Observer<T>> observers;
    private T value;

    public DefaultObservable() {
        this.observers = new ArrayList<>();
    }

    @Override
    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    @Override
    public void removeAllObservers() {
        observers.clear();
    }

    @Override
    public void notifyAllObservers() {
        for (Observer<T> observer : observers) {
            observer.onNotify(value);
        }
    }

    @Override
    public void updateValue(T newValue) {
        value = newValue;
    }

    @Override
    public T getValue() {
        return value;
    }
}

