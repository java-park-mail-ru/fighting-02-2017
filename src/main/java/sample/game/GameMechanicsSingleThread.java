package sample.game;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import sample.controllers.UserController;
import sample.websocket.SocketService;
import support.Answer;
import support.TimeOut;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by andrey on 25.04.17.
 */
@Service
public class GameMechanicsSingleThread {
    @Autowired
    private SocketService socketService;
    @Autowired
    private GameService gameService;
    static final Logger log = Logger.getLogger(UserController.class);
    Answer answer = new Answer();
    private ExecutorService tickExecutor = Executors.newSingleThreadExecutor();
    private AtomicLong id = new AtomicLong(1);
    private @NotNull ConcurrentLinkedQueue<String> waiters = new ConcurrentLinkedQueue<>();
    private @NotNull HashMap<Long, Players> playingNow = new HashMap<>();


    public void addWaiters(String login){
        if (waiters.isEmpty()) {
            System.out.println("Waiting");
            socketService.sendMessageToUser(login, answer.messageClient("Waiting"));
            waiters.add(login);
        } else {
            final Players players = new Players(login, waiters.poll());
            playingNow.put(id.get(), players);
            startGame(players.getLogins());
        }
    }

    public void startGame(ArrayList<String> logins) {
        logins.forEach(item -> socketService.sendMessageToUser(item, answer.messageClient(id.get(), logins)));
        id.getAndIncrement();
    }

    public void gmStep(Players players) {
        final SnapServer snapServer = new SnapServer(players);
        players.getLogins().forEach(item -> socketService.sendMessageToUser(item, snapServer.getJson()));
        socketService.sendMessageToUser(players.getSnaps().get(0).getLogin(), snapServer.getJson());
        socketService.sendMessageToUser(players.getSnaps().get(1).getLogin(), snapServer.getJson());
        if (players.getSnaps().get(0).hp.equals(0) || (players.getSnaps().get(1).hp.equals(0))) {
            playingNow.remove(players.getSnaps().get(0).getId());
            endGame(players.getSnaps());
        } else {
            players.cleanSnaps();
        }
    }

    public void addSnap(SnapClient snap) {
        final Players players = playingNow.get(snap.getId());
        if (players.setAndGetSize(snap) == 2) {
            gmStep(players);
        }
    }

    public void endGame(ArrayList<SnapClient> snaps) {
        snaps.forEach(item -> {
            if (item.hp <= 0)
                socketService.sendMessageToUser(item.getLogin(), answer.messageClient("Game over. You lose."));
            else
                socketService.sendMessageToUser(item.getLogin(), answer.messageClient("Game over. Congratulation! You win."));
            socketService.cutDownConnection(item.getLogin(), CloseStatus.NORMAL);
        });
    }
}


