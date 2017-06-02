package sample.game;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by andrey on 25.04.17.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY )
public class SnapServer {
    public SnapClient first;

    public SnapClient second;

    public Long id;

    private static final Logger log = Logger.getLogger(SnapServer.class);


    SnapServer(Players players, Long id ) {
            this.id=id;
            first = players.getFSnap();
            second = players.getSSnap();
    }
    public JSONObject getJson(){
        final ObjectMapper objectMapper=new ObjectMapper();
        final JSONObject resultJson = new JSONObject();
        try {
            return  resultJson.put("id",id)
                              .put("first",new JSONObject(objectMapper.writeValueAsString(first)))
                              .put("second",new JSONObject(objectMapper.writeValueAsString(second)));
        } catch (JsonProcessingException e) {
            log.error("Json error",e);
        }
        return new JSONObject();
    }
}
