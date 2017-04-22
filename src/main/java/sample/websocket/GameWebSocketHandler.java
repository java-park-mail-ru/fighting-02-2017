package sample.websocket;

import objects.User;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
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
    public GameWebSocketHandler(@NotNull SocketService socketService,@NotNull UserService userService) {
        this.socketService = socketService;
//       this.userService=userService;
    }

   /* public GameWebSocketHandler(){
        this.socketService=new SocketService();
    }*/
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws AuthenticationException {
        System.out.println("Connect");
        final String login = (String) webSocketSession.getAttributes().get(SESSIONKEY);
        if ((login == null)) {//проверить userService
            throw new AuthenticationException("Only authenticated users allowed to play a game");
        }
        socketService.registerUser(login, webSocketSession);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws AuthenticationException {
        final Long userId = (Long) session.getAttributes().get("userId");
        System.out.println("textmessage");
        // final UserProfile user;
        //if (userId == null || (user = accountService.getUserById(Id.of(userId))) == null) {
         //   throw new AuthenticationException("Only authenticated users allowed to play a game");
        //}
        //handleMessage(user, message);
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    private void handleMessage(User user, TextMessage text) {
        System.out.println("message");
     //   final Message message;
     //   try {
     //       message = objectMapper.readValue(text.getPayload(), Message.class);
      //  } catch (IOException ex) {
        //    LOGGER.error("wrong json format at ping response", ex);
       ///     return;
       // }
       // try {
        //    //noinspection ConstantConditions
         //   messageHandlerContainer.handle(message, userProfile.getId());
        //} catch (HandleException e) {
         //   LOGGER.error("Can't handle message of type " + message.getType() + " with content: " + message.getContent(), e);
       // }
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
