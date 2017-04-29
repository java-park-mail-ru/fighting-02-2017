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
    private final Answer answer = new Answer();
    private static final String URL = "*";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public static class URIRequest {
        public static final String login = "/login";
        public static final String signup = "/signup";
        public static final String get = "/get";
        public static final String update = "/update";
        public static final String updateInfo = "/updateinfo";
        public static final String changePass = "/changepass";
        public static final String logOut = "/logout";
        public static final String leaders = "/leaders";

    }

    /* @ExceptionHandler(Exception.class)
     public String handleException(HttpServletRequest request, Exception e){

         final String method=request.getRequestURI().replaceAll("/api/user","");
         if (method.equals(URIRequest.signup))
             return answer.onlyStatus(new HttpStatus().getForbidden());
         if (method.equals(URIRequest.login)||method.equals(URIRequest.get))
             return answer.onlyStatus((new HttpStatus().getNotFound()));
         if (method.equals(URIRequest.update)||method.equals(URIRequest.updateInfo)||method.equals(URIRequest.changePass))
             return answer.onlyStatus(new HttpStatus().getBadRequest());

         return answer.onlyStatus("It is strange");
     }*/
    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.login, method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String loginUser(@RequestBody User body, HttpSession httpSession) {
        final String status = userService.login(body);
        if (status.equals(new HttpStatus().getOk())) httpSession.setAttribute(SESSIONKEY, body.getLogin());
        return answer.withObject(status, body);
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.signup, method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String registerUser(@RequestBody User body) {
        final String status = userService.register(body);
        return answer.onlyStatus(status);
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.get, method = RequestMethod.GET, produces = "application/json")
    public String getUser(HttpSession httpSession) {
        final String login = (String) httpSession.getAttribute(SESSIONKEY);
        if (login == null) return answer.onlyStatus(new HttpStatus().getNotFound());
        final User user = userService.getUser(login);
        return answer.withObject(new HttpStatus().getOk(), user);
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.update, method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String updateUserLogin(@RequestBody User body,
                                  HttpSession httpSession) {
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            final String status = userService.update(body);
            if (status.equals(new HttpStatus().getOk())) {
                httpSession.removeAttribute(SESSIONKEY);
                httpSession.setAttribute(SESSIONKEY, body.getLogin());
            }
            return answer.onlyStatus(status);
        }
        log.error("Unauthorized");
        return answer.onlyStatus(new HttpStatus().getUnauthorized());

    }


    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.updateInfo, method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String updateUserInfo(@RequestBody UsersData body,
                                 HttpSession httpSession) {
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            final UsersData usersData = userService.updateInfo(body);
            if (usersData == null) return answer.onlyStatus(new HttpStatus().getBadRequest());
            return answer.withObject(new HttpStatus().getOk(), usersData);
        }
        System.out.println("Unauthorized");
        log.error("Unauthorized");
        return answer.onlyStatus(new HttpStatus().getUnauthorized());
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.changePass, method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String changeUserPass(@RequestBody User body,
                                 HttpSession httpSession) {
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            final String status = userService.changePass(body);
            return answer.onlyStatus(status);
        }
        System.out.println("Unauthorized");
        log.error("Unauthorized");
        return answer.onlyStatus(new HttpStatus().getUnauthorized());

    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.logOut, method = RequestMethod.GET, produces = "application/json")
    public String logoutUser(HttpSession httpSession) {
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            httpSession.removeAttribute(SESSIONKEY);
            return answer.onlyStatus(new HttpStatus().getOk());
        }

        System.out.println("Bad Request");
        log.error("Bad Request");
        return answer.onlyStatus(new HttpStatus().getBadRequest());
    }

    @CrossOrigin(origins = URL, maxAge = 3600)
    @RequestMapping(path = URIRequest.leaders, method = RequestMethod.GET, produces = "application/json")
    public String getLeaders() {
        try {
            return answer.forLeaders(new HttpStatus().getOk(), userService.getLeaders());
        } catch (JSONException e) {

            System.out.println("Bad Request");
            log.error("Bad Request");
            return answer.onlyStatus(new HttpStatus().getBadRequest());
        }
    }
}