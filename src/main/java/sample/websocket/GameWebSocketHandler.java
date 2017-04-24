package sample.websocket;


import objects.User;
import org.apache.log4j.Logger;
import org.hibernate.service.spi.InjectService;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import sample.game.SnapShot;
import services.UserService;

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

    @ExceptionHandler(Exception.class)
    public void handleException(WebSocketSession webSocketSession, Exception e,CloseStatus closeStatus) throws Exception {
        log.error(e.getMessage());
        afterConnectionClosed(webSocketSession,closeStatus);
    }
    public  GameWebSocketHandler(@NotNull SocketService socketService,@NotNull  UserService userService){
        this.socketService=socketService;
        this.userService=userService;
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws AuthenticationException, IOException {
        System.out.println("Connect");

        final String login = (String) webSocketSession.getAttributes().get(SESSIONKEY);
        if ((login == null)) {
            throw new AuthenticationException("Only authenticated users allowed to play a game");
        }
        socketService.registerUser(login, webSocketSession);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("message","Connect");
        socketService.sendMessageToUser(login,jsonObject);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws AuthenticationException {
        final Long userId = (Long) session.getAttributes().get("userId");
        System.out.println("textmessage");
        try {
            final JSONObject json = new JSONObject(message.getPayload());
            final SnapShot snapShot=new SnapShot(json);
        }
        catch (Exception e){
            System.out.println("Json error");
        }

    }


    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        System.out.println("Disonnect");
        final String login = (String) webSocketSession.getAttributes().get(SESSIONKEY);
        if (login == null) {
            return;
        }
        socketService.removeUser(login);
    }


    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        System.out.println("Transport Problem!!!!!");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
