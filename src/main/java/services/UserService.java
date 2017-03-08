package services;

import com.sun.istack.internal.NotNull;
import objects.HttpStatus;
import objects.ObjUser;
import org.eclipse.jetty.util.Fields;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Denis on 21.02.2017.
 */
@Service
public class UserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public interface Callback {
        void onSuccess(String status);

        void onError(String status);
    }

    public interface CallbackWithUser {
        void onSuccess(String status, ObjUser objUser);

        void onError(String status);
    }


    public UserService(@NotNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void register(ObjUser objUser, Callback callback) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO users (login,password) values(?,?)",
                    objUser.getLogin(),
                    objUser.getPassword());
        } catch (Exception e) {
            callback.onError(new HttpStatus().getForbidden());
        }
        callback.onSuccess(new HttpStatus().getOk());
    }

    public void login(ObjUser objUser, CallbackWithUser callbackWithUser) {
        String SQL = "SELECT * FROM users WHERE (login, password) = (?,?)";
        ObjUser user = null;
        try {
            user = jdbcTemplate.queryForObject(SQL,
                    new Object[]{
                            objUser.getLogin(), objUser.getPassword()}, new UserMapper());
        } catch (Exception e) {
            callbackWithUser.onError(new HttpStatus().getNotFound());
        }
        callbackWithUser.onSuccess(new HttpStatus().getOk(), user);
    }

    public void update(ObjUser newObjUser, CallbackWithUser callbackWithUser) {
        int rownum = jdbcTemplate.update(
                "UPDATE users SET login= ? WHERE login = ?", newObjUser.getNewlogin(), newObjUser.getLogin());

        if (rownum == 0) {
            callbackWithUser.onError(new HttpStatus().getBadRequest());
        }
        callbackWithUser.onSuccess(new HttpStatus().getOk(), newObjUser);
    }

    public void changePass(ObjUser objUser, CallbackWithUser callbackWithUser) {
        String SQL = "UPDATE users SET password= ? WHERE (login,password) =(?,?)";
        int rownum = jdbcTemplate.update(
                SQL, objUser.getNewpassword(), objUser.getLogin(), objUser.getPassword());

        if (rownum == 0) {
            callbackWithUser.onError(new HttpStatus().getBadRequest());
        }
        callbackWithUser.onSuccess(new HttpStatus().getOk(), objUser);
    }

    public JSONArray getLeaders() {
        final JSONArray jsonArray = new JSONArray();

        return jsonArray;
    }
}

