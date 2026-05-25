package team2.mse.ajou.server.domain.item.model;

/**
 *
 * @author Junseo Hwang 202322128
 */
public interface IConsumableItem {
    void useItemIfPossible();
    boolean isAvailable();
}
