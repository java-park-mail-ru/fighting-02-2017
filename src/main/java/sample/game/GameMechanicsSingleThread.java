package sample.game;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sample.websocket.SocketService;
import support.Answer;
import support.Coef;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by andrey on 25.04.17.
 */
@Service
public class GameMechanicsSingleThread {
    @Autowired
    private SocketService socketService;
    @Autowired
    private GameService gameService;

    Answer answer=new Answer();
    private ExecutorService tickExecutor= Executors.newSingleThreadExecutor();
    private @NotNull ConcurrentLinkedQueue<String> waiters = new ConcurrentLinkedQueue<>();
    private @NotNull ConcurrentLinkedQueue<SnapShot> snapshot = new ConcurrentLinkedQueue<>();
    private @NotNull ConcurrentLinkedQueue<Players> playingNow = new ConcurrentLinkedQueue<>();
    private static class Players{
        //login
        Players(String first,String second){
            this.first=first;
            this.second=second;
        }
        String first;
        String second;
    }
    public void addWaiters(String login) throws IOException {
        if (waiters.isEmpty()) {
            System.out.println("Waiting");
            socketService.sendMessageToUser(login,answer.messageClient("Waiting"));
            waiters.add(login);
        } else {
            //   users.add(waiters.peek());
            //  users.add(login);
            startGame(waiters.poll(), login);
        }
    }
    public void startGame(String first,String second) throws IOException {
        playingNow.add(new Players(first,second));
        socketService.sendMessageToUser(first,answer.messageClient("playing "+first+" and "+second));
        System.out.println("playing "+first+" and "+second);
    }

    public void gmStemp(SnapShot snap1,SnapShot snap2) throws IOException {
        final Coef coef1= new Coef();
        coef1.setKMethod(snap1.method);
        coef1.setKBlock(snap1.target,snap2.block);
        snap1.hp=snap1.hp-coef1.damage;

        final Coef coef2= new Coef();
        coef2.setKMethod(snap2.method);
        coef2.setKBlock(snap2.target,snap1.block);
        snap2.hp=snap2.hp-coef2.damage;
        final JSONObject jsonObject=new JSONObject();
        jsonObject.put("hp1",snap1.hp);
        jsonObject.put("hp2",snap2.hp);
        jsonObject.put("damage1",coef1.damage);
        jsonObject.put("damage2",coef2.damage);
        final Players players=playingNow.poll();
        socketService.sendMessageToUser(players.first,jsonObject);
        socketService.sendMessageToUser(players.second,jsonObject);
    }
    public void addSnap(SnapShot snap) throws IOException {
        if (snapshot.isEmpty()) {
            System.out.println("Waiting");
            snapshot.add(snap);
        } else {
            gmStemp(snapshot.poll(), snap);
        }
    }
}

