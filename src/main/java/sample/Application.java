package sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import sample.websocket.GameWebSocketHandler;
import services.UserService;
import support.GameData;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Denis on 21.02.2017.
 */

@SpringBootApplication
@ComponentScan(basePackages = {"services", "sample"})
public class Application {
    private static final Logger log = Logger.getLogger(UserService.class);

    public static void main(String[] args) {
        SpringApplication.run(new Object[]{WebSocketConfig.class, Application.class}, args);
        final InputStream inJson = GameData.class.getResourceAsStream("/InitialData.json");
        try {
            final GameData data = new ObjectMapper().readValue(inJson, GameData.class);
            data.setCoef();
        } catch (IOException e) {
            log.error("wrong json in resource file");
        }
    }

    @Bean
    public WebSocketHandler gameWebSocketHandler() {
        return new PerConnectionWebSocketHandler(GameWebSocketHandler.class);
    }

}