package team2.mse.ajou.server.domain.shared.observer;

/**
 * Observer: Default observer implementation.
 */
public class DefaultObserver<T> implements Observer<T> {
    private final NotifyCallback<T> callback;

    public DefaultObserver(NotifyCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onNotify(T newValue) {
        callback.onNotify(newValue);
    }
}