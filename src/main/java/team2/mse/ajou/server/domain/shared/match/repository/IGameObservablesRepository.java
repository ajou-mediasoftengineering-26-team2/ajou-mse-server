package team2.mse.ajou.server.domain.shared.match.repository;

/**
 * Combined repository of `IMatchObservablesRepository` and `IPlayerObservablesRepository`.
 */
public interface IGameObservablesRepository extends IMatchObservablesRepository, IPlayerObservablesRepository {
    
}
