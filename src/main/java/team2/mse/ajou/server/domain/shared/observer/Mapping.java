package team2.mse.ajou.server.domain.shared.observer;

public interface Mapping<T, R> {
    R mapValue(T value);
}
