package objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Denis on 21.02.2017.
 */
public class ObjUser {
    private String id;
    private String login;
    private String password;
    private String email;
    private String nickname;
    private String sessionkey;

    public ObjUser() {

    }

    @JsonCreator
    public ObjUser(
            @JsonProperty("id") String id,
            @JsonProperty("login") String login,
            @JsonProperty("password") String password,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("sessionkey") String sessionkey,
            @JsonProperty("email") String email) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.sessionkey = sessionkey;

        //System.out.println(this.id + ' ' + this.login + ' ' + this.password + ' ' + this.nickname + ' ' + this.email);
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionkey() {
        return sessionkey;
    }

    public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }

    public JSONObject getJson() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("login", login);
        jsonObject.put("password", password);
        jsonObject.put("nickname", nickname);
        jsonObject.put("email", email);
        jsonObject.put("sessionkey", sessionkey);
        return jsonObject;
    }

}


