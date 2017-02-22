package sample.controllers;

import objects.HttpStatus;
import objects.ObjUser;
import org.eclipse.jetty.util.Fields;
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

    public interface CallbackLogin {
        void onSuccess(String status, ObjUser objUser);

        void onError(String status);
    }

    private Map<String, ObjUser> db;

    public AccountService() {
        db = new HashMap<>();
        IDGEN = new AtomicLong(0);
    }

    void register(ObjUser objUser, Callback callback) {
        objUser.setId(String.valueOf(IDGEN.getAndIncrement()));
        if (db.put(objUser.getLogin(), objUser) == null) {
            callback.onSuccess(new HttpStatus().getCreated());
        } else {
            IDGEN.getAndDecrement();
            callback.onError(new HttpStatus().getForbidden());
        }
    }

    void login(ObjUser objUser, CallbackLogin callbackLogin) {
        final String login = objUser.getLogin();
        final String password = objUser.getPassword();
        if (db.get(login) != null && db.get(login).getPassword().equals(password)) {
            callbackLogin.onSuccess(new HttpStatus().getOk(), db.get(login));
        } else {
            callbackLogin.onError(new HttpStatus().getNotFound());
        }
    }

    void update(ObjUser newObjUser, CallbackLogin callback) {
        final String login = newObjUser.getLogin();
        if (login != null && db.get(login) != null) {
            final ObjUser currObjUser = db.get(login);
            final Field[] currFields = currObjUser.getClass().getDeclaredFields();
            final Field[] newFields = newObjUser.getClass().getDeclaredFields();
            for (int i = 0; i < newFields.length; i++) {
                final Field newField = newFields[i];
                newField.setAccessible(true);
                try {
                    if (newField.get(newObjUser) != null && !newField.getName().equals("password")) {
                        currFields[i].setAccessible(true);
                        currFields[i].set(currObjUser, newField.get(newObjUser));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    callback.onError(new HttpStatus().getBadRequest());
                }
            }
            if (db.replace(login, currObjUser) != null) {
                callback.onSuccess(new HttpStatus().getOk(), currObjUser);
            } else {
                callback.onError(new HttpStatus().getBadRequest());
            }
        } else {
            callback.onError(new HttpStatus().getBadRequest());
        }
    }

    void changePass(ObjUser prevUser, ObjUser newUser, CallbackLogin callback) {
        final String login = prevUser.getLogin();

        if (login != null && db.get(login) != null) {
            final ObjUser currObjUser = db.get(login);
            if (currObjUser.getPassword().equals(prevUser.getPassword())) {
                currObjUser.setPassword(newUser.getPassword());
                if (db.replace(login, currObjUser) != null) {
                    callback.onSuccess(new HttpStatus().getOk(), currObjUser);
                } else {
                    callback.onError(new HttpStatus().getBadRequest());
                }
            } else {
                callback.onError(new HttpStatus().getForbidden());
            }
        } else {
            callback.onError(new HttpStatus().getBadRequest());
        }
    }
}

