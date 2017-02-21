package sample.controllers;

import objects.ObjSessionKey;
import objects.ObjUser;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
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
        accountService.login(body.getLogin(), body.getPassword(), new AccountService.Callback() {
            @Override
            public void onSuccess() {
                final long id = IDGEN.getAndIncrement();
                answer.put("status", "200 OK");
                answer.put("result", "success");
                answer.put("user_key", String.valueOf(id));
                httpsession.setAttribute(String.valueOf(id), body);
            }

            @Override
            public void onError() {
                answer.put("status", "200 OK");
                answer.put("result", "error");
            }
        });
        return answer.toString();
    }

    @RequestMapping(path = "/signup", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String registerUser(@RequestBody ObjUser body) {
        final JSONObject answer = new JSONObject();
        accountService.register(body, new AccountService.Callback() {
            @Override
            public void onSuccess() {
                answer.put("status", "200 OK");
                answer.put("result", "success");
            }

            @Override
            public void onError() {
                answer.put("status", "200 OK");
                answer.put("result", "error");
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
                answer.put("status", "200 OK");
                answer.put("result", "success");
                answer.put("user_status", "logged");
                answer.put("user", httpSession.getAttribute(key));
                /*JSONObject user = new JSONObject();
                try {
                    Method getNickname = httpSession.getAttribute(key).getClass().getMethod("getNickname");
                    getNickname.invoke(httpSession.getAttribute(key).getClass().getConstructor().newInstance());
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }*/

            } else {
                answer.put("status", "200 OK");
                answer.put("result", "success");
                answer.put("user_status", "unlogged");
            }
        } else {
            answer.put("status", "400 Bad Request");
            answer.put("result", "error");
        }
        return answer.toString();
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String updateUser(@RequestBody ObjUser body) {
        final JSONObject answer = new JSONObject();
        accountService.update(body, new AccountService.Callback() {
            @Override
            public void onSuccess() {
                answer.put("status", "200 OK");
                answer.put("result", "success");
            }

            @Override
            public void onError() {
                answer.put("status", "200 OK");
                answer.put("result", "error");
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
            answer.put("status", "200 OK");
            answer.put("result", "success");
        } else {
            answer.put("status", "400 Bad Request");
            answer.put("result", "error");
        }
        return answer.toString();
    }
}
