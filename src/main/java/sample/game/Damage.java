package sample.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sample.websocket.Message;
import support.GameData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by andrey on 09.05.17.
 */
public final class Damage {
    private static final Logger log = Logger.getLogger(Damage.class);
    public Integer baseDamage;
    public Double MethodHead;
    public Double MethodArm;
    public Double MethodLeg;
    public Double BlockHead;
    public Double BlockBody;

    private Damage() {
    }

    private static Damage instance;

    public static Damage getInstance() {
        if (instance == null) {
            final InputStream inJson = GameData.class.getResourceAsStream("/InitialData.json");
            try {
                instance = new ObjectMapper().readValue(inJson, Damage.class);

            } catch (IOException e) {
                log.error("wrong json in resource file",e);
            }
        }
        return instance;
    }

    public Double setKMethod(String method) {
        Double kProb = 1.0;
        if (method.equals("arm")) kProb *= MethodArm;
        if (method.equals("head")) kProb *= MethodHead;
        if (method.equals("leg")) kProb *= MethodLeg;
        return kProb;
    }

    public Double setKBlock(String target, String block, Double kProb) {
        if (target.equals(block)) {
            if (block.equals("body")) kProb *= BlockBody;
            else kProb *= BlockHead;
        } else kProb = kProb / 2.0;
        return kProb;
    }

    public @NotNull Integer setAndGetDamage(Double kProb) {
        return (int) Math.round((1.0 - kProb) * baseDamage);
    }

    public ArrayList<SnapClient> getSnapsWithDamage(ArrayList<SnapClient> snaps) {
        final ArrayList<Integer> damage = new ArrayList<>();
        calculate(snaps.get(0),snaps.get(1).block);
        calculate(snaps.get(1),snaps.get(0).block);
        return snaps;
    }
    private void calculate(SnapClient snap, String block){
        if(snap.method==null) {
            snap.setTakenDamage(0);
            return;
        }
        final Double kProb = setKBlock(snap.target, block, setKMethod(snap.method));
        final Integer damage = setAndGetDamage(kProb);
        snap.hp = Math.max(snap.hp - damage, 0);
        snap.setTakenDamage(damage);
    }

}
