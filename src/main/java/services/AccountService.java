package services;

import objects.HttpStatus;
import objects.ObjUser;
import org.eclipse.jetty.util.Fields;
import org.json.JSONArray;
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
public class AccountService {
    private AtomicLong IDGEN;

    public interface Callback {
        void onSuccess(String status);

        void onError(String status);
    }

    public interface CallbackWithUser {
        void onSuccess(String status, ObjUser objUser);

        void onError(String status);
    }

    private Map<String, ObjUser> db;

    public AccountService() {
        db = new HashMap<>();
        IDGEN = new AtomicLong(0);
    }

    public void register(ObjUser objUser, Callback callback) {
        objUser.setId(String.valueOf(IDGEN.getAndIncrement()));
        if (db.put(objUser.getLogin(), objUser) == null) {
            callback.onSuccess(new HttpStatus().getOk());
        } else {
            IDGEN.getAndDecrement();
            callback.onError(new HttpStatus().getForbidden());
        }
    }

    public void login(ObjUser objUser, CallbackWithUser callbackWithUser) {
        final String login = objUser.getLogin();
        final String password = objUser.getPassword();
        if (db.get(login) != null && db.get(login).getPassword().equals(password)) {
            callbackWithUser.onSuccess(new HttpStatus().getOk(), db.get(login));
        } else {
            callbackWithUser.onError(new HttpStatus().getNotFound());
        }
    }

    public void update(ObjUser newObjUser, CallbackWithUser callbackWithUser) {
        final String login = newObjUser.getLogin();
        final String newlogin = newObjUser.getNewlogin();
        if (login != null && newlogin != null && db.get(login) != null) {
            final ObjUser objUser = db.get(login);
            objUser.setLogin(newlogin);
            if (db.remove(login) != null && db.put(newlogin, objUser) == null) {
                callbackWithUser.onSuccess(new HttpStatus().getOk(), objUser);
            } else {
                callbackWithUser.onError(new HttpStatus().getBadRequest());
            }
        } else {
            callbackWithUser.onError(new HttpStatus().getBadRequest());
        }
    }

    public void changePass(ObjUser objUser, CallbackWithUser callbackWithUser) {
        final String login = objUser.getLogin();
        final String password = objUser.getPassword();
        final String newpassword = objUser.getNewpassword();
        if (login != null && db.get(login) != null && password != null && newpassword != null) {
            final ObjUser currObjUser = db.get(login);
            if (currObjUser.getPassword().equals(password)) {
                currObjUser.setPassword(newpassword);
                if (db.replace(login, currObjUser) != null) {
                    callbackWithUser.onSuccess(new HttpStatus().getOk(), currObjUser);
                } else {
                    callbackWithUser.onError(new HttpStatus().getBadRequest());
                }
            } else {
                callbackWithUser.onError(new HttpStatus().getForbidden());
            }
        } else {
            callbackWithUser.onError(new HttpStatus().getBadRequest());
        }
    }

    public JSONArray getLeaders(){
        final JSONArray jsonArray = new JSONArray();
        for(Map.Entry<String, ObjUser> entry : db.entrySet()) {
            jsonArray.put(entry.getValue().getJson());
        }
        return jsonArray;
    }
}

