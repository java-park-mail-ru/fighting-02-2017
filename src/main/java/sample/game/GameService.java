package sample.game;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by andrey on 24.04.17.
 */
@Service
public class GameService {
    private @NotNull ConcurrentLinkedQueue<String> waiters = new ConcurrentLinkedQueue<>();

    public void addWaiters(String login) {
        if (waiters.isEmpty()) {
            System.out.println("Waiting");
            waiters.add(login);
        } else {
            startGame(waiters.poll(), login);
        }
    }
    public void startGame(String login1,String login2){
        System.out.println("playing "+login1+" and "+login2);
    }
}


