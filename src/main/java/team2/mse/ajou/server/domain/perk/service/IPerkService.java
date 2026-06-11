package team2.mse.ajou.server.domain.perk.service;

import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;
import java.util.UUID;

/**
 * Perk Service
 * @author Junseo Hwang 202322128
 */
public interface IPerkService {
    /**
     * This save player's perk choice
     * @param id uuid of player who has chosen perk
     * @param perk perk chosen by player
     */
    void putPerkChoice(UUID id, PERK perk);

    /**
     * This save player's perk ack
     * Not use (due to a change in the design)
     * @param id uuid of player who has sent ack
     */
    void putAck(UUID id);

    /**
     * This returns a list of all perks that the player does not have.
     * @param playerData player
     * @return perk list
     */
    List<PERK> getUnownedPerks(PlayerData playerData);

    /**
     * This updates each player's perkChoiceList
     * @param matchData current match
     */
    void giveRandomPerkChoiceList(MatchData matchData);
}
