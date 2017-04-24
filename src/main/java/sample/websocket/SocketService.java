package sample.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import sample.game.GameService;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andrey on 16.04.17.
 */
@Service
public class SocketService {
    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    @Autowired
    private GameService gameService;
    public void registerUser(@NotNull String login, @NotNull WebSocketSession webSocketSession) {
        sessions.put(login, webSocketSession);
        try {
            gameService.addWaiters(login);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public boolean isConnected(@NotNull String login) {
        return sessions.containsKey(login) && sessions.get(login).isOpen();
    }
    public void removeUser(@NotNull String  login)
    {
        sessions.remove(login);
    }

    public void cutDownConnection(@NotNull String login, @NotNull CloseStatus closeStatus) {
        final WebSocketSession webSocketSession = sessions.get(login);
        if (webSocketSession != null && webSocketSession.isOpen()) {
            try {
                webSocketSession.close(closeStatus);
            } catch (IOException ignore) {
            }
        }
    }

    public void sendMessageToUser(@NotNull String login, @NotNull JSONObject json) throws IOException {
        final WebSocketSession webSocketSession = sessions.get(login);
        if (webSocketSession == null) {
            throw new IOException("no game websocket for user " + login);
        }
        if (!webSocketSession.isOpen()) {
            throw new IOException("session is closed or not exsists");
        }
        try {
            webSocketSession.sendMessage(new TextMessage(json.toString()));
        } catch (JsonProcessingException | WebSocketException e) {
            throw new IOException("Unnable to send message", e);
        }
    }
}
