package team2.mse.ajou.server.domain.firebase.lobby;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.shared.LobbyData;

import java.util.UUID;

/**
 * FRDB 로비 수정 서비스
 *
 * @author yubin
 */
@Service
public class FrdbLobbyService {
    private final DatabaseReference lobbyRef;
    private final FirebaseDatabase firebaseDatabase;

    public FrdbLobbyService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;

        lobbyRef = firebaseDatabase
                .getReference("lobbies");
    }

    public DatabaseReference getLobbyRef(UUID id) {
        return lobbyRef
                .child(id.toString());
    }

    public void setLobby(UUID id, LobbyData lobbyData) {
        getLobbyRef(id).setValueAsync(FrdbLobbyData.from(lobbyData));
    }

    public void clearAllLobby() {
        lobbyRef.removeValueAsync();
    }
}
