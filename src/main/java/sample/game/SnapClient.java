package sample.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * Created by andrey on 24.04.17.
 */
public class SnapClient {
    private Long id;
    @JsonIgnore
    private String login;
    String method;
    String block;
    String target;
    Integer hp;
   @JsonCreator
    public SnapClient( @JsonProperty("id") Long id,
                        @JsonProperty("method") String method,
                       @JsonProperty("block") String block,
                       @JsonProperty("target") String target,
                       @JsonProperty("hp") Integer hp) {
        this.id=id;
        this.method=method;
        this.block=block;
        this.target=target;
        this.hp=hp;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public Long getId() {
        return id;
    }
}
