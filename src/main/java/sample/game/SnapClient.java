package sample.game;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * Created by andrey on 24.04.17.
 */
public class SnapClient {
    private Long id;
    private String login;
    String method;
    String block;
    String target;
    Integer hp;

    public @Nullable SnapClient(JSONObject json){
        id = Long.parseLong(json.get("id").toString());
        // System.out.println("id Ok");
        method = json.get("method").toString();
        block = json.get("block").toString();
        target = json.get("target").toString();
        hp = new Integer(json.get("hp").toString());
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
