package sample.controllers;

import objects.HttpStatus;
import objects.User;
import objects.UsersData;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;
import services.UserService;
import support.Answer;

import javax.servlet.http.HttpSession;

/**
 * Created by andrey on 21.02.2017.
 */

@RestController
@RequestMapping("/api/user")
public class UserController {
    static final Logger log = Logger.getLogger(UserController.class);
    private final UserService userService;
    private static final String SESSIONKEY = "user";
    private static final String URL = "*";

    public UserController(UserService userService) {

        this.userService = userService;
    }

    public static class URIRequest {
        public static final String LOGIN = "/login";
        public static final String SIGNUP = "/signup";
        public static final String GET = "/get";
        public static final String UPDATE = "/update";
        public static final String UPDATEINFO = "/updateinfo";
        public static final String CHANGEPASS = "/changepass";
        public static final String LOGOUT = "/logout";
        public static final String LEADERS = "/leaders";

    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.LOGIN, method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String loginUser(@RequestBody User body, HttpSession httpSession) {
        final UsersData usersData = userService.login(body);
        if (usersData != null) {
            httpSession.setAttribute(SESSIONKEY, body.getLogin());
            return Answer.withObject(new HttpStatus().getOk(), usersData);
        }
        return Answer.onlyStatus(new HttpStatus().getNotFound());
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.SIGNUP, method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String registerUser(@RequestBody User body, HttpSession httpSession) {
        final UsersData usersData = userService.register(body);
        if (usersData != null) {
            httpSession.setAttribute(SESSIONKEY, body.getLogin());
            return Answer.withObject(new HttpStatus().getOk(), usersData);
        }
        return Answer.onlyStatus(new HttpStatus().getForbidden());
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.GET, method = RequestMethod.GET, produces = "application/json")
    public String getUser(HttpSession httpSession) {
        final String login = (String) httpSession.getAttribute(SESSIONKEY);
        if (login == null) return Answer.onlyStatus(new HttpStatus().getNotFound());
        final UsersData usersData = userService.getUser(login);
        if (usersData != null) return Answer.withObject(new HttpStatus().getOk(), usersData);
        return Answer.onlyStatus(new HttpStatus().getNotFound());
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.UPDATE, method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String updateUserLogin(@RequestBody User body,
                                  HttpSession httpSession) {
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            final String status = userService.update(body);
            if (status.equals(new HttpStatus().getOk())) {
                httpSession.removeAttribute(SESSIONKEY);
                httpSession.setAttribute(SESSIONKEY, body.getLogin());
            }
            return Answer.onlyStatus(status);
        }
        log.error("Unauthorized");
        return Answer.onlyStatus(new HttpStatus().getUnauthorized());

    }


    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.UPDATEINFO, method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String updateUserInfo(@RequestBody UsersData body,
                                 HttpSession httpSession) {
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            final UsersData usersData = userService.updateInfo(body);
            if (usersData == null) return Answer.onlyStatus(new HttpStatus().getBadRequest());
            return Answer.withObject(new HttpStatus().getOk(), usersData);
        }
        log.error("Unauthorized");
        return Answer.onlyStatus(new HttpStatus().getUnauthorized());
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.CHANGEPASS, method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String changeUserPass(@RequestBody User body,
                                 HttpSession httpSession) {
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            final String status = userService.changePass(body);
            return Answer.onlyStatus(status);
        }
        log.error("Unauthorized");
        return Answer.onlyStatus(new HttpStatus().getUnauthorized());

    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.LOGOUT, method = RequestMethod.GET, produces = "application/json")
    public String logoutUser(HttpSession httpSession) {
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            httpSession.removeAttribute(SESSIONKEY);
            return Answer.onlyStatus(new HttpStatus().getOk());
        }

        log.error("Bad Request");
        return Answer.onlyStatus(new HttpStatus().getBadRequest());
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.LEADERS, method = RequestMethod.GET, produces = "application/json")
    public String getLeaders() {
        try {
            return Answer.forLeaders(new HttpStatus().getOk(), userService.getLeaders());
        } catch (JSONException e) {

            log.error("Bad Request");
            return Answer.onlyStatus(new HttpStatus().getBadRequest());
        }
    }
}