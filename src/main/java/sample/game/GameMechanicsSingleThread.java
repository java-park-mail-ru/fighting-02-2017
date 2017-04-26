package sample.game;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import sample.controllers.UserController;
import sample.websocket.SocketService;
import support.Answer;
import support.Coef;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
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
    //private @NotNull ConcurrentLinkedQueue<SnapClient> snapshot = new ConcurrentLinkedQueue<>();
    private @NotNull ConcurrentHashMap<Long, Players> playingNow = new ConcurrentHashMap<>();

    private static class Players {
        private String first;
        private String second;
        private boolean snapFlag = false;
        private SnapClient moreEarlySnap;

        Players(@NotNull String first, @NotNull String second) {
            this.first = first;
            this.second = second;
        }

        public void setSnap(@NotNull SnapClient snap) {
            this.moreEarlySnap = snap;
            snapFlag = true;
        }

        public boolean snapExist() {
            return snapFlag;
        }
        public void setFlagFalse(){snapFlag=false;}
        public @NotNull SnapClient getSnap() {
            return moreEarlySnap;
        }
    }

    public void addWaiters(String login) throws IOException {
        if (waiters.isEmpty()) {
            System.out.println("Waiting");
            socketService.sendMessageToUser(login, answer.messageClient("Waiting"));
            waiters.add(login);
        } else {
            startGame(waiters.poll(), login);
        }
    }

    public void startGame(String first, String second) throws IOException {
        socketService.sendMessageToUser(first, answer.messageClient(id.get()));
        socketService.sendMessageToUser(second, answer.messageClient(id.get()));
        playingNow.put(id.get(), new Players(first, second));
        id.getAndIncrement();
    }
    public boolean CompareWithZero(Double num){
        return Math.abs(num)<0.0000001;
    }

    public void gmStemp(SnapClient snap1, SnapClient snap2)  {
        final Coef coef1 = new Coef();
        coef1.setKMethod(snap1.method);
        coef1.setKBlock(snap1.target, snap2.block);
        coef1.setDamage();
        snap1.hp = snap1.hp - coef1.damage;
        if(snap1.hp<0.0) snap1.hp=0.0;

        final Coef coef2 = new Coef();
        coef2.setKMethod(snap2.method);
        coef2.setKBlock(snap2.target, snap1.block);
        coef2.setDamage();
        snap2.hp = snap2.hp - coef2.damage;
        if(snap2.hp<0.0) snap2.hp=0.0;
        final SnapServer snapServer=new SnapServer(snap1,snap2,coef1.damage,coef2.damage);
            socketService.sendMessageToUser(snap1.getLogin(), snapServer.getResult());
            socketService.sendMessageToUser(snap2.getLogin(), snapServer.getResult());
            if (CompareWithZero(snap1.hp) || (CompareWithZero(snap2.hp))){
                playingNow.remove(snap1.getId());
                endGame(snap1, snap2);
            }
            else {
                final Players players = playingNow.get(snap1.getId());
                players.setFlagFalse();
            }

    }

    public void addSnap(SnapClient snap)  {
        final Players players = playingNow.get(snap.getId());
        if (players.snapExist()) {
            gmStemp(players.getSnap(), snap);
        } else {
            players.setSnap(snap);
        }
    }
    public void endGame(SnapClient snap1,SnapClient snap2) {
        if (CompareWithZero(snap1.hp)&&CompareWithZero(snap2.hp)){
            socketService.sendMessageToUser(snap1.getLogin(), answer.messageClient("Game over. Draw"));
            socketService.sendMessageToUser(snap2.getLogin(), answer.messageClient("Game over. Draw"));
        }
        else{
            if(CompareWithZero(snap1.hp)){
                socketService.sendMessageToUser(snap1.getLogin(), answer.messageClient("Game over. You lose."));
                socketService.sendMessageToUser(snap2.getLogin(), answer.messageClient("Game over. Congratulation! You win."));
            }
            else {
                if (CompareWithZero(snap2.hp)) {
                    socketService.sendMessageToUser(snap2.getLogin(), answer.messageClient("Game over. You lose."));
                    socketService.sendMessageToUser(snap1.getLogin(), answer.messageClient("Game over. Congratulation! You win."));
                }
                else{
                    log.error("Logic error");
                    socketService.cutDownConnection(snap1.getLogin(), CloseStatus.SERVER_ERROR);
                    socketService.cutDownConnection(snap2.getLogin(), CloseStatus.SERVER_ERROR);
                    return;
                }
            }
        }
        socketService.cutDownConnection(snap1.getLogin(), CloseStatus.NORMAL);
        socketService.cutDownConnection(snap2.getLogin(), CloseStatus.NORMAL);
    }
}


