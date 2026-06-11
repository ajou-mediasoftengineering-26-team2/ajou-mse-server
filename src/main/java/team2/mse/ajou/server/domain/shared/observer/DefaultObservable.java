package team2.mse.ajou.server.domain.shared.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Observer: Default observable implementation.
 */
public class DefaultObservable<T> implements Observable<T> {
    private final List<Observer<T>> observers;
    private T value;
    private T previousValue;

    private final boolean isIgnoreDuplicateValue;
    private final boolean isNotifyOnSubscribe;

    public DefaultObservable(
            T initialValue,
            boolean isIgnoreDuplicateValue,
            boolean isNotifyOnSubscribe
    ) {
        this.isIgnoreDuplicateValue = isIgnoreDuplicateValue;
        this.isNotifyOnSubscribe = isNotifyOnSubscribe;

        this.previousValue = initialValue;
        this.value = initialValue;
        this.observers = new ArrayList<>();
    }

    @Override
    public void addObserver(Observer<T> observer) {
        observers.add(observer);

        if (isNotifyOnSubscribe) {
            observer.onNotify(value);
        }
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
        previousValue = value;
        value = newValue;

        if (!isIgnoreDuplicateValue || !Objects.equals(previousValue, newValue)) {
            notifyAllObservers();
        }
    }

    @Override
    public T getValue() {
        return value;
    }
}

