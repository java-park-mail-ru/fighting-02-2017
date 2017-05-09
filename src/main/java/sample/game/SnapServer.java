package sample.game;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    SnapServer(Map<SnapClient, Integer> map) {
        final JSONObject resultJson = new JSONObject();
        AtomicInteger i = new AtomicInteger(0);
        map.forEach((snapClient, takenDamage) -> {
            final JSONObject json = new JSONObject();
            json.put("login", snapClient.getLogin());
            json.put("hp", snapClient.hp);
            json.put("takenDamage", takenDamage);
            json.put("block", snapClient.block);
            json.put("method", snapClient.method);
            json.put("target", snapClient.target);
            if (i.getAndIncrement() == 0) {
                resultJson.put("id", snapClient.getId());
                resultJson.put("first", json);
            } else resultJson.put("second", json);
        });
        result = resultJson;
    }

    public JSONObject getResult() {
        return result;
    }
}
