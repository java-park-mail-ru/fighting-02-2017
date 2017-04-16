package sample.controllers;

import objects.HttpStatus;
import objects.User;
import objects.UsersData;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import services.UserService;
import support.Answer;

import javax.servlet.http.HttpSession;

/**
 * Created by Denis on 21.02.2017.
 */

@RestController
@RequestMapping("/api/user")
public class UserController {
    static final Logger log = Logger.getLogger(UserController.class);
    private final UserService userService;
    private static final String SESSIONKEY = "user";
    private static final String URL = "https://tp-front-end-js-game.herokuapp.com";
    public UserController(JdbcTemplate jdbcTemplate) {
        this.userService = new UserService(jdbcTemplate);
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String loginUser(@RequestBody User body, HttpSession httpSession) {
        final Answer answer = new Answer();
        final String status=userService.login(body);
               if(status.equals(new HttpStatus().getOk())) {
                   answer.withObject(status, body);
                   httpSession.setAttribute(SESSIONKEY, body.getLogin());
               }
            else answer.onlyStatus(status);
        return answer.getResult();
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = "/signup", method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String registerUser(@RequestBody User body) {

        final Answer answer = new Answer();
        final String status=userService.register(body);
        answer.onlyStatus(status);
        return answer.getResult();
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = "/get", method = RequestMethod.GET, produces = "application/json")
    public String getUser(HttpSession httpSession) {

        final Answer answer = new Answer();
        final String login = (String) httpSession.getAttribute(SESSIONKEY);
        final Object result=userService.getUser(login);
        if(result.getClass().equals(User.class)){
            answer.withObject(new HttpStatus().getOk(), (User)result);
        }
        else answer.onlyStatus(result.toString());
        return answer.getResult();
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = "/update", method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String updateUser(@RequestBody User body,
                             HttpSession httpSession) {

        final Answer answer = new Answer();
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            final Object result=userService.update(body);
                    if(result.getClass().equals(User.class)) {
                        httpSession.removeAttribute(SESSIONKEY);
                        httpSession.setAttribute(SESSIONKEY, ((User)result).getLogin());
                        answer.onlyStatus(new HttpStatus().getOk());
                    }
                    else answer.onlyStatus(result.toString());
        }
        else {
            log.error("Unauthorized");
            answer.onlyStatus(new HttpStatus().getUnauthorized());
        }
        return answer.getResult();
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = "/updateinfo", method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String updateUserInfo(@RequestBody UsersData body,
                                 HttpSession httpSession) {
        final Answer answer = new Answer();
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            final Object result = userService.updateInfo(body);
                    if(result.getClass().equals(UsersData.class)){
                    answer.onlyStatus(new HttpStatus().getOk());
                }
                else answer.onlyStatus(result.toString());
        } else {
            log.error("Unauthorized");
            answer.onlyStatus(new HttpStatus().getUnauthorized());
        }
        return answer.getResult();
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = "/changepass", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String changeUserPass(@RequestBody User body,
                                 HttpSession httpSession) {
        final Answer answer = new Answer();
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            final String status=userService.changePass(body);
            answer.onlyStatus(status);
        } else {
            log.error("Unauthorized");
            answer.onlyStatus(new HttpStatus().getUnauthorized());
        }
        return answer.getResult();
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = "/logout", method = RequestMethod.GET, produces = "application/json")
    public String logoutUser(HttpSession httpSession) {

        final Answer answer = new Answer();
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            httpSession.removeAttribute(SESSIONKEY);
            answer.onlyStatus(new HttpStatus().getOk());
        } else {
            log.error("Bad Request");
            answer.onlyStatus(new HttpStatus().getBadRequest());
        }
        return answer.getResult();
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = "/leaders", method = RequestMethod.GET, produces = "application/json")
    public String getLeaders() {
        final Answer answer = new Answer();
        try {
            answer.forLeaders(new HttpStatus().getOk(), userService.getLeaders());
        } catch (JSONException e) {
            log.error("Bad Request");
            answer.onlyStatus(new HttpStatus().getBadRequest());
        }
        return answer.getResult();
    }
}