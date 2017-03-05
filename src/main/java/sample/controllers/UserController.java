package sample.controllers;

import objects.HttpStatus;
import objects.ObjUser;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
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
    private final String SESSIONKEY = "user";

    public UserController() {
        this.accountService = new AccountService();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String loginUser(@RequestBody ObjUser body, HttpSession httpSession) {
        final JSONObject answer = new JSONObject();
        accountService.login(body, new AccountService.CallbackWithUser() {
            @Override
            public void onSuccess(String status, ObjUser objUser) {
                answer.put("status", status);
                answer.put("user", objUser.getJson());
                httpSession.setAttribute(SESSIONKEY, objUser);
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

    @RequestMapping(path = "/get", method = RequestMethod.GET, produces = "application/json")
    public String getUser(HttpSession httpSession) {
        final JSONObject answer = new JSONObject();
        final ObjUser objUser = (ObjUser) httpSession.getAttribute(SESSIONKEY);
        if (objUser != null) {
            answer.put("status", new HttpStatus().getOk());
            answer.put("user", objUser.getJson());
        } else {
            answer.put("status", new HttpStatus().getUnauthorized());
        }
        return answer.toString();
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String updateUser(@RequestBody ObjUser body, HttpSession httpSession) {
        final JSONObject answer = new JSONObject();
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            accountService.update(body, new AccountService.CallbackWithUser() {
                @Override
                public void onSuccess(String status, ObjUser objUser) {
                    httpSession.removeAttribute(SESSIONKEY);
                    httpSession.setAttribute(SESSIONKEY, objUser);
                    answer.put("status", status);
                }

                @Override
                public void onError(String status) {
                    answer.put("status", status);
                }
            });
        } else {
            answer.put("status", new HttpStatus().getUnauthorized());
        }
        return answer.toString();
    }

    @RequestMapping(path = "/changepass", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String changeUserPass(@RequestBody ObjUser body, HttpSession httpSession) {
        final JSONObject answer = new JSONObject();
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            accountService.changePass(body, new AccountService.CallbackWithUser() {
                @Override
                public void onSuccess(String status, ObjUser objUser) {
                    httpSession.removeAttribute(SESSIONKEY);
                    httpSession.setAttribute(SESSIONKEY, objUser);
                    answer.put("status", status);
                }

                @Override
                public void onError(String status) {
                    answer.put("status", status);
                }
            });
        } else {
            answer.put("status", new HttpStatus().getUnauthorized());
        }
        return answer.toString();
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String logoutUser(HttpSession httpSession) {
        final JSONObject answer = new JSONObject();
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            httpSession.removeAttribute(SESSIONKEY);
            answer.put("status", new HttpStatus().getOk());
        } else {
            answer.put("status", new HttpStatus().getBadRequest());
        }
        return answer.toString();
    }
}
