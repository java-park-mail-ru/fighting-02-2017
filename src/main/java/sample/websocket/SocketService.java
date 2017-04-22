package sample.websocket;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andrey on 16.04.17.
 */
@Service
public class SocketService {
    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void registerUser(@NotNull String login, @NotNull WebSocketSession webSocketSession) {
        sessions.put(login, webSocketSession);
    }

    public void removeUser(@NotNull String  login)
    {
        sessions.remove(login);
    }

}
