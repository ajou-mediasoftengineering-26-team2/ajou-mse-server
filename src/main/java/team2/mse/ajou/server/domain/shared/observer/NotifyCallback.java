package team2.mse.ajou.server.domain.shared.observer;

public interface NotifyCallback<T> {
    public void onNotify(T value);
}
