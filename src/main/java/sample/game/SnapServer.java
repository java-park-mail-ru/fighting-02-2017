package sample.game;

import org.json.JSONObject;

/**
 * Created by andrey on 25.04.17.
 */
public class SnapServer {
    private JSONObject result;
    private String first;
    private String second;

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    SnapServer(SnapClient snap1, SnapClient snap2, Integer takenDamage1, Integer takenDamage2) {
        final JSONObject resultJson = new JSONObject();
        resultJson.put("id", snap1.getId());
        for (int i = 1; i <= 2; i++) {
            final SnapClient snap;
            final Integer takenDamage;
            if (i == 1) {
                snap = snap1;
                takenDamage = takenDamage1;
            } else {
                snap = snap2;
                takenDamage = takenDamage2;
            }
            final JSONObject json = new JSONObject();
            json.put("login", snap.getLogin());
            if (i == 1) first = snap.getLogin();
            if (i == 2) second = snap.getLogin();
            json.put("hp", snap.hp);
            json.put("takenDamage", takenDamage);
            json.put("block", snap.block);
            json.put("method", snap.method);
            json.put("target", snap.target);
            if (i == 1) resultJson.put("first", json);
            else resultJson.put("second", json);
        }
        result = resultJson;
    }

    public JSONObject getResult() {
        return result;
    }
}
