package sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import sample.game.GameService;
import sample.websocket.GameWebSocketHandler;
import sample.websocket.SocketService;
import services.UserService;
import sample.controllers.UserController;
import support.Coef;
import support.GameData;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by Denis on 21.02.2017.
 */

@SpringBootApplication
@ComponentScan(basePackages = {"services","sample"})
public class Application {
    public static final long IDLE_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(1);
    public static final int BUFFER_SIZE_BYTES = 8192;
    public static void main(String[] args) {
        SpringApplication.run(new Object[]{WebSocketConfig.class, Application.class}, args);
        final InputStream inJson = GameData.class.getResourceAsStream("/InitialData.json");
        try {
            final GameData data = new ObjectMapper().readValue(inJson, GameData.class);
            data.setCoef();
        } catch (IOException e) {
            System.out.println("wrong json");
        }
    }

    @Bean
    public WebSocketHandler gameWebSocketHandler() {
        return new PerConnectionWebSocketHandler(GameWebSocketHandler.class);
    }

}