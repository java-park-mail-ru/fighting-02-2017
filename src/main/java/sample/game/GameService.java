package sample.game;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sample.websocket.SocketService;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by andrey on 24.04.17.
 */
@Service
public class GameService {
    @Autowired
    private SocketService socketService;
    @Autowired
    private GameMechanicsSingleThread gameMechanicsSingleThread;
    private ExecutorService tickExecutor= Executors.newSingleThreadExecutor();
    private @NotNull ConcurrentLinkedQueue<String> waiters = new ConcurrentLinkedQueue<>();
    private static class Players{
        //login
        String first;
        String second;
    }
    public void addWaiters(String login) {
        tickExecutor.submit(() -> {
            try {
                gameMechanicsSingleThread.addWaiters(login);
            } catch (IOException ignore) {}
        });
    }

    public void addSnap(SnapClient snap){
        tickExecutor.submit(() -> gameMechanicsSingleThread.addSnap(snap));

    }
}


