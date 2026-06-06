package team2.mse.ajou.server.domain.shared.observer;

public interface InheritableMappedObservable<T, R> extends Observable<R> {
    void addDownstreamObservable(InheritableMappedObservable<R, ?> observer);
    void removeDownstreamObservable(InheritableMappedObservable<R, ?> observer);
    void removeAllDownstreamObservables();
    void notifyAllDownstreamObservables();

    R mapValue(T value);
}