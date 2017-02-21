package objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Denis on 21.02.2017.
 */
public class ObjUser {
    private String login;
    private String password;
    private String email;
    private String nickname;

    public ObjUser() {

    }

    @JsonCreator
    public ObjUser(@JsonProperty("login") String login,
                   @JsonProperty("password") String password,
                   @JsonProperty("nickname") String nickname,
                   @JsonProperty("email") String email) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.email = email;

        System.out.println(this.login + ' ' + this.password + ' ' + this.nickname + ' ' + this.email);
    }

    public String getKey() {
        return login + password;
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
}


