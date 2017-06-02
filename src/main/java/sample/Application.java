package sample;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import sample.game.Damage;
import sample.websocket.GameWebSocketHandler;

/**
 * Created by Denis on 21.02.2017.
 */

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = {"services", "sample"})
public class Application {
    @Autowired
    Damage damage;

    private static final Logger log = Logger.getLogger(Application.class);

    public static void main(String[] args) {
         SpringApplication.run(new Object[]{WebSocketConfig.class, Application.class}, args);
    }

    @Bean
    public WebSocketHandler gameWebSocketHandler() {
        damage.resourseUp();
        return new PerConnectionWebSocketHandler(GameWebSocketHandler.class);
    }

}