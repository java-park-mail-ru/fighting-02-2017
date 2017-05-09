package sample.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import support.GameData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by andrey on 09.05.17.
 */
@JsonIgnoreProperties({"kProb", "damage"})
public final class Damage {
    private static final Logger log = Logger.getLogger(Damage.class);
    public Integer baseDamage;
    public Double MH;
    public Double MA;
    public Double ML;
    public Double BH;
    public Double BB;

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
        if (method.equals("arm")) kProb *= MA;
        if (method.equals("head")) kProb *= MH;
        if (method.equals("leg")) kProb *= ML;
        return kProb;
    }

    public Double setKBlock(String target, String block, Double kProb) {
        if (target.equals(block)) {
            if (block.equals("body")) kProb *= BB;
            else kProb *= BH;
        } else kProb = kProb / 2.0;
        return kProb;
    }

    public @NotNull Integer setAndGetDamage(Double kProb) {
        return (int) Math.round((1.0 - kProb) * baseDamage);
    }

    public ArrayList<Integer> calculate(ArrayList<SnapClient> snap) {
        final ArrayList<Integer> damage = new ArrayList<>();
        for (int i = 0, j = 1; i < 2; i++, j--) {
            final SnapClient item = snap.get(i);
            final SnapClient anotherItem = snap.get(j);
            final Double kProb = setKBlock(item.target, anotherItem.block, setKMethod(item.method));
            final Integer dam = setAndGetDamage(kProb);
            item.hp = Math.max(item.hp - dam, 0);
            damage.add(dam);
        }
        return damage;
    }
}
