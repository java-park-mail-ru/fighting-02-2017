package objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Denis on 21.02.2017.
 */
public class ObjSessionKey {

    private String key;

    public ObjSessionKey(){

    }

    @JsonCreator
    public ObjSessionKey(@JsonProperty("key") String key) {
        this.key = key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
