package team2.mse.ajou.server.domain.shared.match.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * Match turn/game logic calculation handling service.
 *
 * @author Ahn Yubin / 202021088
 */
@Service
public class MatchTurnCalcService {
    /**
     * Initializes given `MatchData` for the first turn of the match.
     * @param matchData Match data to be modified.
     */
    public void initializeMatch(MatchData matchData) {
        int playerIdx = matchData.getCurrentPlayerIdx();
        int attackerIdx = matchData.getAttackerPlayerIdx();

        List<PlayerData> players = matchData.getPlayers();
        // System.out.println("Players: " + players);
        if (players.size() < 2) {
            throw new ApiError(5005, "Insufficient players in the match!");
        }

        // Attacking player setup.
        PlayerData attackerPlayer = players.get(attackerIdx);
        attackerPlayer.setAttacking(true);
        attackerPlayer.setSelecting(true);

        // Defending player setup.
        PlayerData defencePlayer = players.get((attackerIdx + 1) % players.size());
        defencePlayer.setAttacking(false);
        defencePlayer.setSelecting(false);

        // Reset HP to full, moves to "Shake over hands" etc.
        for (PlayerData player : players) {
            player.setHp(10);
            player.setWins(0);
            player.setChoice(HAND_CHOICE.SHAKE_OVER_HANDS);
        }

        // First turn goes for attacking player.
        matchData.setState(MATCH_STATE.GAME_ATK_CHOICE);
    }

    /**
     * Calculates a single turn from given `MatchData`.
     * @param matchData Match data to be modified.
     */
    public void calculateTurn(MatchData matchData) {
        MATCH_STATE state = matchData.getState();
        List<PlayerData> players = matchData.getPlayers();

        int playerIdx = matchData.getCurrentPlayerIdx();
        int attackerIdx = matchData.getAttackerPlayerIdx();
        int defenceIdx = (attackerIdx + 1) % players.size();

        if (players.size() < 2) {
            throw new ApiError(5005, "Insufficient players in the match!");
        }

        // Attacking player reference.
        PlayerData attackerPlayer = players.get(attackerIdx);
        HAND_CHOICE attackerChoice = attackerPlayer.getChoice();
        // Defending player reference.
        PlayerData defencePlayer = players.get(defenceIdx);
        HAND_CHOICE defenceChoice = defencePlayer.getChoice();

        boolean isAttackSuccess = false;
        // Has attacking player KO'd the defending player?
        boolean isPlayerKO = false;

        // BEGIN DAMAGE CALCULATION LOGIC --------------------------
        switch (state) {
            // GAME_ATK_CHOICE -> GAME_DEF_CHOICE
            case GAME_ATK_CHOICE:
                // Time over. Switch to defending players turn...
                matchData.setState(MATCH_STATE.GAME_DEF_CHOICE);
                break;
            // GAME_DEF_CHOICE -> GAME_ATK_CHOICE
            case GAME_DEF_CHOICE:
                // Time over. Calculate outcome...
                // For now whe criteria for attacks to land is whether two player has chosen different moves
                isAttackSuccess = attackerChoice != defenceChoice;

                // TODO: ADD ON-DAMAGE PERK EFFECTS ETC
                if (isAttackSuccess) {
                    // Deal damage to defending player.
                    // FIXME: CONSTANT DAMAGE (2) FOR NOW.
                    int damageAmount = 2;

                    defencePlayer.setHp(Math.max(0, defencePlayer.getHp() - damageAmount));
                    isPlayerKO = (defencePlayer.getHp() <= 0);
                } else {
                    // Defending success! Switch the roles around.
                    attackerIdx = (attackerIdx + players.size() + 1) % players.size();
                    isPlayerKO = false;
                }

                // Switch to attacking players turn and increment turns counter...
                matchData.setState(MATCH_STATE.GAME_ATK_CHOICE);
                matchData.setCurrentTurn(matchData.getCurrentTurn() + 1);
                break;
        }

        // If player is defeated, then grant one point to the attacker.
        if (isPlayerKO) {
            attackerPlayer.setWins(attackerPlayer.getWins() + 1);
            matchData.setState(MATCH_STATE.GAME_ROUND_END_PLAYER_KO);
        }

        // Select appropriate player index to let them choose their moves.
        if (matchData.getState() == MATCH_STATE.GAME_ATK_CHOICE) {
            playerIdx = attackerIdx;
        } else {
            playerIdx = defenceIdx;
        }
        // END DAMAGE CALCULATION LOGIC --------------------------

        // Update match data.
        matchData.setAttackSuccess(isAttackSuccess);
        matchData.setCurrentPlayerIdx(playerIdx);
        matchData.setAttackerPlayerIdx(attackerIdx);

        // Update player data.
        for (int i = 0; i < players.size(); i++) {
            PlayerData player = players.get(i);

            // Is this player selecting?
            if (i == playerIdx) {
                System.out.println("PLAYER " + player.getUsername() + " SELECTS!");
                player.setSelecting(true);

                // Reset player movement so that no selection = force select "shake over hands".
                player.setChoice(HAND_CHOICE.SHAKE_OVER_HANDS);
            } else {
                player.setSelecting(false);
            }

            // Is this player attacker?
            if (i == attackerIdx) {
                System.out.println("PLAYER " + player.getUsername() + " ATTACKS!");
                player.setAttacking(true);
            } else {
                player.setAttacking(false);
            }
        }

        // (FIXME) End game as soon as player downs another.
        if (isPlayerKO) {
            int winnerPlayerIdx = -1,
                winsMax = -1;

            for (int i = 0; i < players.size(); i++) {
                PlayerData player = players.get(i);

                if (player.getWins() > winsMax) {
                    winnerPlayerIdx = i;
                    winsMax = player.getWins();
                }
            }

            if (winnerPlayerIdx != -1) {
                players.get(winnerPlayerIdx).setFinalWinner(true);
            }
            matchData.setWinnerPlayerIdx(winnerPlayerIdx);

            matchData.setCurrentRound(matchData.getCurrentRound() + 1);
            matchData.setCurrentTurn(0);
            matchData.setState(MATCH_STATE.END_RESULT);
        }
    }
}
