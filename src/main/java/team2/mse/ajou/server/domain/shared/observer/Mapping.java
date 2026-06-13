package team2.mse.ajou.server.domain.shared.observer;

/**
 * Converts one type of value to another. Used for `FlowMappedObservable`.
 */
public interface Mapping<T, R> {
    R mapValue(T value);
}
