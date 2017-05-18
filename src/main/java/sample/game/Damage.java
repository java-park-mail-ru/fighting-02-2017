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
                System.out.println(e.getMessage());
                log.error("wrong json in resource file");
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

    public ArrayList<Integer> calculate(ArrayList<SnapClient> snaps) {
        final ArrayList<Integer> damage = new ArrayList<>();
        String blockOpponent=snaps.get(1).block;
            for(SnapClient item:snaps) {
                final Double kProb = setKBlock(item.target, blockOpponent, setKMethod(item.method));
                final Integer dam = setAndGetDamage(kProb);
                item.hp = Math.max(item.hp - dam, 0);
                damage.add(dam);
                blockOpponent = snaps.get(0).block;
            }
        return damage;
    }
}
