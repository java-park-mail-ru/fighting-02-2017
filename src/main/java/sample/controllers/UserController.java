package sample.controllers;

import objects.HttpStatus;
import objects.ObjSessionKey;
import objects.ObjUser;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import services.AccountService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Denis on 21.02.2017.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final AccountService accountService;
    private AtomicLong IDGEN;

    public UserController() {
        this.accountService = new AccountService();
        IDGEN = new AtomicLong(0);
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String loginUser(@RequestBody ObjUser body, HttpSession httpsession) {
        final JSONObject answer = new JSONObject();
        accountService.login(body, new AccountService.CallbackUser() {
            @Override
            public void onSuccess(String status, ObjUser objUser) {
                final long httpSessionId = IDGEN.getAndIncrement();
                answer.put("status", status);
                answer.put("sessionkey", String.valueOf(httpSessionId));
                answer.put("user", objUser.getJson());
                httpsession.setAttribute(String.valueOf(httpSessionId), objUser);
            }

            @Override
            public void onError(String status) {
                answer.put("status", status);
            }
        });
        return answer.toString();
    }

    @RequestMapping(path = "/signup", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String registerUser(@RequestBody ObjUser body) {
        final JSONObject answer = new JSONObject();
        accountService.register(body, new AccountService.Callback() {
            @Override
            public void onSuccess(String status) {
                answer.put("status", status);
            }

            @Override
            public void onError(String status) {
                answer.put("status", status);
            }
        });
        return answer.toString();
    }

    @RequestMapping(path = "/get", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String getUser(@RequestBody ObjSessionKey objKey, HttpSession httpSession) {
        final JSONObject answer = new JSONObject();
        final String key = objKey.getKey();

        if (key != null) {
            if (httpSession.getAttribute(key) != null) {
                answer.put("status", new HttpStatus().getOk());
                answer.put("user", ((ObjUser) httpSession.getAttribute(key)).getJson());
            } else {
                answer.put("status", new HttpStatus().getUnauthorized());
            }
        } else {
            answer.put("status", new HttpStatus().getBadRequest());
        }
        return answer.toString();
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String updateUser(@RequestBody ObjUser body, HttpSession httpSession) {
        final JSONObject answer = new JSONObject();
        accountService.update(body, new AccountService.CallbackUser() {
            @Override
            public void onSuccess(String status, ObjUser objUser) {
                if(body.getSessionkey() != null){
                    httpSession.setAttribute(body.getSessionkey(), objUser);
                }
                answer.put("status", status);
            }

            @Override
            public void onError(String status) {
                answer.put("status", status);
            }
        });
        return answer.toString();
    }

    @RequestMapping(path = "/changepass", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String changeUserPass(@RequestBody ArrayList<ObjUser> body, HttpSession httpSession) {
        final JSONObject answer = new JSONObject();
        final String sessionKey = body.get(0).getSessionkey();

        System.out.println(body.get(0));
        System.out.println(body.get(1));

        accountService.changePass(body.get(0), body.get(1), new AccountService.CallbackUser() {
            @Override
            public void onSuccess(String status, ObjUser objUser) {
                if(sessionKey != null){
                    httpSession.setAttribute(sessionKey, objUser);
                }
                answer.put("status", status);
            }

            @Override
            public void onError(String status) {
                answer.put("status", status);
            }
        });
        return answer.toString();
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String logoutUser(@RequestBody ObjSessionKey objKey, HttpSession httpSession) {
        final JSONObject answer = new JSONObject();
        final String key = objKey.getKey();
        if (key != null) {
            httpSession.removeAttribute(key);
            answer.put("status", new HttpStatus().getOk());
        } else {
            answer.put("status", new HttpStatus().getBadRequest());
        }
        return answer.toString();
    }
}
