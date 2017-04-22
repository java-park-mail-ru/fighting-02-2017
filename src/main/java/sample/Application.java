package sample;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import sample.websocket.GameWebSocketHandler;
import sample.websocket.SocketService;
import services.UserService;
import sample.controllers.UserController;
import java.util.concurrent.TimeUnit;

/**
 * Created by Denis on 21.02.2017.
 */

@SpringBootApplication
public class Application {
    public static final long IDLE_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(1);
    public static final int BUFFER_SIZE_BYTES = 8192;
    public static void main(String[] args) {
        SpringApplication.run(new Object[]{WebSocketConfig.class, Application.class}, args);
    }

    @Bean
    public WebSocketHandler gameWebSocketHandler() {
        return new PerConnectionWebSocketHandler(GameWebSocketHandler.class);
      //  return new GameWebSocketHandler();
    }
 @Autowired
 private BeanFactory beanFactory;

  /*  @Bean
    public WebSocketHandler myHandler() {
        final PerConnectionWebSocketHandler perConnectionWebSocketHandler = new PerConnectionWebSocketHandler(GameWebSocketHandler.class);
        perConnectionWebSocketHandler.setBeanFactory(beanFactory);
        return perConnectionWebSocketHandler;
    }*/


}