package team2.mse.ajou.server.domain.turn.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.turn.model.GameMatch;
import team2.mse.ajou.server.domain.turn.model.Player;
import team2.mse.ajou.server.domain.turn.repository.IPlayerRepository;

/**
 * @author Junseo Hwang
 */

@Service
public class DamageCalcService {
    private final IPlayerRepository playerRepository;

    DamageCalcService(IPlayerRepository playerRepository){
        this.playerRepository = playerRepository;
    }

    public void calcDamage(GameMatch gameMatch) {
        Player player1 = playerRepository.findById(gameMatch.getPlayer1Id())
                .orElseThrow(()-> new IllegalArgumentException("Not Found: " +gameMatch.getPlayer1Id()));
        Player player2 = playerRepository.findById(gameMatch.getPlayer2Id())
                .orElseThrow(()-> new IllegalArgumentException("Not Found: " +gameMatch.getPlayer1Id()));

        if(player1.isAttacker()){
            calcDamage(player1, player2);
        }
        else{
            calcDamage(player2, player1);
        }
    }

    private void calcDamage(Player attacker, Player defender){
        // 공격이 성공한 경우
        if(attacker.getChoice().equalsIgnoreCase(defender.getChoice())){

            // 데미지 로직

        }

        // 공격이 실패한 경우(방어에 성공한 경우)
        else{
            // 방어 로직

            attacker.setAttacker(false);
            defender.setAttacker(true);
        }
    }
}
