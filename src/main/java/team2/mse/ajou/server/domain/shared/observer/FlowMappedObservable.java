package team2.mse.ajou.server.domain.shared.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Kotlin의 Flow 비슷한, 하위 Observable을 가지며 값을 맵핑해 보낼 수 있는 Observable
 *
 * @author Ahn Yubin / 202021088
 */
public class FlowMappedObservable<T, R> implements Observer<T>, Observable<R> {
    private final List<Observer<R>> observers;
    private final List<FlowMappedObservable<R, ?>> downstreamObservables;
    private R value;
    private R previousValue;

    private final boolean isIgnoreNull;
    private final boolean isIgnoreDuplicateValue;
    private final boolean isNotifyOnSubscribe;

    private final Mapping<T, R> mapping;

    public FlowMappedObservable(
            R initialValue,
            boolean isIgnoreNull,
            boolean isIgnoreDuplicateValue,
            boolean isNotifyOnSubscribe,
            Mapping<T, R> mapping
    ) {
        this.isIgnoreNull = isIgnoreNull;
        this.isIgnoreDuplicateValue = isIgnoreDuplicateValue;
        this.isNotifyOnSubscribe = isNotifyOnSubscribe;

        this.previousValue = initialValue;
        this.value = initialValue;
        this.downstreamObservables = new ArrayList<>();
        this.observers = new ArrayList<>();

        this.mapping = mapping;
    }

    @Override
    public void onNotify(T newValue) {
        if (isIgnoreNull && newValue == null) {
            return;
        }

        updateValue(mapValue(newValue));
    }

    public void addObserver(Observer<R> observer) {
        observers.add(observer);

        if (isNotifyOnSubscribe) {
            observer.onNotify(value);
        }
    }

    public void removeObserver(Observer<R> observer) {
        observers.remove(observer);
    }

    public void removeAllObservers() {
        observers.clear();
    }

    public void notifyAllObservers() {
        for (var observer : observers) {
            observer.onNotify(value);
        }
    }

    public void updateValue(R newValue) {
        previousValue = value;
        value = newValue;

        if (!isIgnoreDuplicateValue || !Objects.equals(newValue, previousValue)) {
            notifyAllObservers();
            notifyAllDownstreamObservables();
        }
    }

    public R getValue() {
        return value;
    }

    public void addDownstreamObservable(FlowMappedObservable<R, ?> observer) {
        downstreamObservables.add(observer);

        if (isNotifyOnSubscribe) {
            observer.onNotify(value);
        }
    }

    public void removeDownstreamObservable(FlowMappedObservable<R, ?> observer) {
        downstreamObservables.remove(observer);
    }

    public void removeAllDownstreamObservables() {
        downstreamObservables.clear();
    }

    public void notifyAllDownstreamObservables() {
        for (var observer : downstreamObservables) {
            observer.onNotify(value);
        }
    }

    public R mapValue(T value) {
        return mapping.mapValue(value);
    }
}
