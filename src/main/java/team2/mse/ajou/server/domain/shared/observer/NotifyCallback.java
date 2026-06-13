package team2.mse.ajou.server.domain.shared.observer;

/**
 * Used for `DefaultObserver`. Callback to be called once a new value were notified.
 */
public interface NotifyCallback<T> {
    public void onNotify(T value);
}
