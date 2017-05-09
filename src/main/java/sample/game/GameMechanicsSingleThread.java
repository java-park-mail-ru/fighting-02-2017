package sample.game;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import sample.controllers.UserController;
import sample.websocket.SocketService;
import support.Answer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
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


    public void addWaiters(String login) throws IOException {
        if (waiters.isEmpty()) {
            System.out.println("Waiting");
            socketService.sendMessageToUser(login, answer.messageClient("Waiting"));
            waiters.add(login);
        } else {
            startGame(new ArrayList<String>() {{
                add(waiters.poll());
                add(login);
            }});
        }
    }

    public void startGame(ArrayList<String> logins) {
        logins.forEach(item -> socketService.sendMessageToUser(item, answer.messageClient(id.get(), logins)));
        playingNow.put(id.get(), new Players(logins));
        id.getAndIncrement();
    }

    public void gmStemp(ArrayList<SnapClient> snaps) {
        final SnapServer snapServer = new SnapServer(new HashMap<SnapClient, Integer>() {{
            AtomicInteger i = new AtomicInteger(0);
            Damage.getInstance().calculate(snaps).forEach(item -> put(snaps.get(i.getAndIncrement()), item));
        }});
        socketService.sendMessageToUser(snaps.get(0).getLogin(), snapServer.getResult());
        socketService.sendMessageToUser(snaps.get(1).getLogin(), snapServer.getResult());
        if (snaps.get(0).hp.equals(0) || (snaps.get(1).hp.equals(0))) {
            playingNow.remove(snaps.get(0).getId());
            endGame(snaps);
        } else {
            final Players players = playingNow.get(snaps.get(0).getId());
            players.setSnap(null);
        }
    }

    public void addSnap(SnapClient snap) {
        final Players players = playingNow.get(snap.getId());
        if (players.getSnap() != null) {
            //gmStemp(players.getSnap(), snap);
            gmStemp(new ArrayList<SnapClient>() {{
                add(players.getSnap());
                add(snap);
            }});
        } else {
            players.setSnap(snap);
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


