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
        userService.login(body, new UserService.CallbackWithUser<User>() {
            @Override
            public void onSuccess(String status, User user) {
                answer.withObject(status, user);
                httpSession.setAttribute(SESSIONKEY, user.getLogin());
            }

            @Override
            public void onError(String status) {
                answer.onlyStatus(status);
            }
        });
        return answer.getResult();
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = "/signup", method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String registerUser(@RequestBody User body) {

        final Answer answer = new Answer();
        userService.register(body, new UserService.Callback() {
            @Override
            public void onSuccess(String status) {
                answer.onlyStatus(status);
            }

            @Override
            public void onError(String status) {
                answer.onlyStatus(status);
            }
        });
        return answer.getResult();
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = "/get", method = RequestMethod.GET, produces = "application/json")
    public String getUser(HttpSession httpSession) {

        final Answer answer = new Answer();
        final String login = (String) httpSession.getAttribute(SESSIONKEY);
        userService.getUser(login, new UserService.CallbackWithUser<User>() {
            @Override
            public void onSuccess(String status, User user) {

                if (user != null) answer.withObject(status, user);
                else {
                    log.error("Unauthorized");
                    answer.onlyStatus(new HttpStatus().getUnauthorized());
                }
            }

            @Override
            public void onError(String status) {
                answer.onlyStatus(status);
            }
        });
        return answer.getResult();
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = "/update", method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String updateUser(@RequestBody User body,
                             HttpSession httpSession) {

        final Answer answer = new Answer();
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            userService.update(body, new UserService.CallbackWithUser<User>() {
                @Override
                public void onSuccess(String status, User user) {
                    httpSession.removeAttribute(SESSIONKEY);
                    httpSession.setAttribute(SESSIONKEY, user.getLogin());
                    answer.onlyStatus(status);
                }

                @Override
                public void onError(String status) {
                    answer.onlyStatus(status);
                }
            });
        } else {
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
            userService.updateInfo(body, new UserService.CallbackWithUser<UsersData>() {
                @Override
                public void onSuccess(String status, UsersData objUser) {
                    //final String login = (String) httpSession.getAttribute(SESSIONKEY);
                    httpSession.removeAttribute(SESSIONKEY);
                    httpSession.setAttribute(SESSIONKEY, objUser.getLogin());
                    answer.onlyStatus(status);
                }

                @Override
                public void onError(String status) {
                    answer.onlyStatus(status);
                }
            });
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
            userService.changePass(body, new UserService.CallbackWithUser<User>() {
                @Override
                public void onSuccess(String status, User user) {
                    final String login = (String) httpSession.getAttribute(SESSIONKEY);
                    httpSession.removeAttribute(SESSIONKEY);
                    httpSession.setAttribute(SESSIONKEY, login);
                    answer.onlyStatus(status);
                }

                @Override
                public void onError(String status) {
                    answer.onlyStatus(status);
                }
            });
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