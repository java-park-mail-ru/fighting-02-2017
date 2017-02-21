package sample.controllers;

import objects.ObjUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 21.02.2017.
 */
@Service
public class AccountService {
    public interface Callback {
        void onSuccess();

        void onError();
    }

    private Map<String, ObjUser> db;

    public AccountService() {
        db = new HashMap<>();
    }

    void register(ObjUser objUser, Callback callback) {
        if (db.put(objUser.getLogin(), objUser) == null) {
            callback.onSuccess();
        } else {
            callback.onError();
        }
    }

    void login(String login, String password, Callback callback) {
        if (db.get(login) != null && db.get(login).getPassword().equals(password)) {
            callback.onSuccess();
        } else {
            callback.onError();
        }
    }

    void update(ObjUser objUser, Callback callback) {
        if (db.replace(objUser.getLogin(), objUser) == null) {
            callback.onSuccess();
        } else {
            callback.onError();
        }
    }
}

