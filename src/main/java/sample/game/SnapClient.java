package sample.game;

import jdk.nashorn.api.scripting.JSObject;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import support.Coef;

import java.util.Locale;

/**
 * Created by andrey on 24.04.17.
 */
public class SnapClient {
    private Long id;
    private String login;
    String method;
    String block;
    String target;
    Double hp;
    public @Nullable SnapClient(JSONObject json) throws Exception{
            id=Long.parseLong(json.get("id").toString());
            System.out.println("id Ok");
            method = json.get("method").toString();
            block=json.get("block").toString();
            target=json.get("target").toString();
            hp=(Double) json.get("hp");
    }
    public void setLogin(String login){this.login=login;}
    public String getLogin(){return login;}
    public Long getId(){return id;}
}
