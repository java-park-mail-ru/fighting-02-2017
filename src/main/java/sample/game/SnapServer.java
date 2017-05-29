package sample.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by andrey on 25.04.17.
 */
public class SnapServer {
    private String first;
    private String second;
    private ArrayList<SnapClient> snapsClient=new ArrayList<>();
    public String getFirst() {
        return first;
    }
    private static final Logger log = Logger.getLogger(SnapServer.class);

    public String getSecond() {
        return second;
    }


    SnapServer(Players players) {
        snapsClient=Damage.getInstance().getSnapsWithDamage(players.getSnaps());
    }
    public JSONObject getJson(){
        final ObjectMapper objectMapper=new ObjectMapper();
        final JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("id",snapsClient.get(0).getId());
            resultJson.put("first",new JSONObject(objectMapper.writeValueAsString(snapsClient.get(0))));
            resultJson.put("second",new JSONObject(objectMapper.writeValueAsString(snapsClient.get(1))));
        } catch (JsonProcessingException e) {
            log.error("Json error",e);
        }
        return resultJson;
    }
}
