package team2.mse.ajou.server.domain.turn.service;

import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.turn.model.GameMatch;
import team2.mse.ajou.server.domain.turn.model.Player;
import team2.mse.ajou.server.domain.turn.repository.IGameMatchRepository;
import team2.mse.ajou.server.domain.turn.repository.IPlayerRepository;

import java.beans.Transient;

/**
 * @author Junseo Hwang
 */

@Service
public class TurnService {
    private final FirebaseDatabase firebaseDatabase;
    private final IPlayerRepository playerRepository;
    private final IGameMatchRepository gameMatchRepository;

    @Autowired
    public TurnService(FirebaseDatabase firebaseDatabase,
                       IPlayerRepository playerRepository,
                       IGameMatchRepository gameMatchRepository) {
        this.firebaseDatabase = firebaseDatabase;
        this.playerRepository = playerRepository;
        this.gameMatchRepository = gameMatchRepository;
    }

    public void updateFireBase(String id, String choice) throws Exception {
        // As an admin, the app has access to read and write all data, regardless of Security Rules
        DatabaseReference ref = firebaseDatabase.getReference();
        ref.child(id)
           .child("choice")
           .setValueAsync(choice);
    }

    @Transient
    public void updateDB(String id, String choice){
        Player player = playerRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: " +id));

        player.setChoice(choice);
        player.setSelected(true);

        GameMatch gameMatch = gameMatchRepository.findById(player.getGameMatchId())
                .orElseThrow(() -> new IllegalArgumentException("No exist Match"));
    }
}
