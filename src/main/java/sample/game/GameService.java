package sample.game;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sample.websocket.SocketService;
import support.Coef;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by andrey on 24.04.17.
 */
@Service
public class GameService {
    @Autowired
    private SocketService socketService;
    private @NotNull ConcurrentLinkedQueue<String> waiters = new ConcurrentLinkedQueue<>();
    private @NotNull ConcurrentLinkedQueue<SnapShot> snapshot = new ConcurrentLinkedQueue<>();
    private @NotNull ConcurrentLinkedQueue<String> users = new ConcurrentLinkedQueue<>();
    public void addWaiters(String login) {
        if (waiters.isEmpty()) {
            System.out.println("Waiting");
            waiters.add(login);
        } else {
            users.add(waiters.peek());
            users.add(login);
            startGame(waiters.poll(), login);
        }
    }
    public void startGame(String login1,String login2){
        System.out.println("playing "+login1+" and "+login2);

    }

   // public Integer hit1To2(SnapShot snap1,SnapShot snap2){
    //
    //}
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
        socketService.sendMessageToUser(users.poll(),jsonObject);
        socketService.sendMessageToUser(users.poll(),jsonObject);
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


