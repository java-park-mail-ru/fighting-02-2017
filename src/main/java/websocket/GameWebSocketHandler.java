package websocket;

import objects.HttpStatus;
import objects.User;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import services.UserService;
import sun.plugin.util.UserProfile;

import javax.naming.AuthenticationException;
import java.io.IOException;

/**
 * Created by andrey on 16.04.17.
 */
public class GameWebSocketHandler extends TextWebSocketHandler {
    private static final String SESSIONKEY = "user";
    private static final Logger log = Logger.getLogger(UserService.class);
    private @NotNull UserService userService;
    private @NotNull SocketService socketService;
    public GameWebSocketHandler(@NotNull UserService userService,  //???? где вызывается?
                                @NotNull SocketService socketService) {
        this.socketService = socketService;
        this.userService=userService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws AuthenticationException {
        final String login = (String) webSocketSession.getAttributes().get(SESSIONKEY);// где set?????????????????????????
        if (login == null) {//проверить userService.getUserById(Id.of(id)) == null
            throw new AuthenticationException("Only authenticated users allowed to play a game");
        }
        socketService.registerUser(login, webSocketSession);
    }



    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        final String login = (String) webSocketSession.getAttributes().get("userId");
        if (login == null) {
            return;
        }
        socketService.removeUser(login);
    }
}
