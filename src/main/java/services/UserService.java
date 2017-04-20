package services;

import objects.HttpStatus;
import objects.User;
import objects.UsersData;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import services.mappers.UserMapper;

import java.util.List;

/**
 * Created by andrey on 21.02.2017.
 */
@Service
public class UserService {
    private static final Logger log = Logger.getLogger(UserService.class.getName());
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public interface Callback {
        void onSuccess(String status);

        void onError(String status);
    }

    public interface CallbackWithUser<T> {
        void onSuccess(String status, T objUser);

        void onError(String status);
    }

    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void register(User user, Callback callback) {
        if (user.getPassword().isEmpty() || user.getLogin().isEmpty()) {
            callback.onError(new HttpStatus().getForbidden());
        } else if (user.getPassword().length() < 8) {
            log.error("Forbidden");
            callback.onError(new HttpStatus().getForbidden());
        } else if (user.getLogin().matches("[а-яА-ЯёЁ]+")) {
            log.error("Forbidden");
            callback.onError(new HttpStatus().getForbidden());
        } else if (user.getLogin().length() < 4) {
            log.error("Forbidden");
            callback.onError(new HttpStatus().getForbidden());
        } else {
            try {
                registerUser(user);
                callback.onSuccess(new HttpStatus().getOk());
            } catch (RuntimeException e) {
                log.error("Forbidden");
                callback.onError(new HttpStatus().getForbidden());
            }
        }
    }

    @Transactional
    private void registerUser(User user) {
        jdbcTemplate.update("insert into users (login,password) values (?,?);", user.getLogin(), user.getHashPassword());
        jdbcTemplate.update("INSERT INTO usersData (login) values (?);", user.getLogin());
    }

    @Transactional
    private int updateUsers(User user) {
        int rowNumb = jdbcTemplate.update("update users set login=? where login=?;", user.getNewlogin(), user.getLogin());
        rowNumb += jdbcTemplate.update("update usersdata set login=? where login=?;", user.getNewlogin(), user.getLogin());
        return rowNumb;
    }

    @Transactional
    private boolean checkINputPasAndLog(User user) {
        final String SQL = "SELECT * FROM users WHERE login = ?";
        final User userDB = jdbcTemplate.queryForObject(SQL,
                new Object[]{user.getLogin()}, new UserMapper());
        return user.comparePass.test(userDB.getPassword());
    }

    public void login(User user, CallbackWithUser callbackWithUser) {
        try {
            if (checkINputPasAndLog(user)) callbackWithUser.onSuccess(new HttpStatus().getOk(), user);
            else {
                log.error("Not Found");
                callbackWithUser.onError(new HttpStatus().getNotFound());
            }
        } catch (Exception e) {
            callbackWithUser.onError(new HttpStatus().getNotFound());
            log.error("Not Found");
        }
    }

    public void getUser(String login, CallbackWithUser callbackWithUser) {
        try {
            final String SQL = "SELECT * FROM users WHERE login = ?";
            final User userDB = jdbcTemplate.queryForObject(SQL,
                    new Object[]{login}, new UserMapper());
            callbackWithUser.onSuccess(new HttpStatus().getOk(), userDB);
        } catch (DataAccessException e) {
            log.error("Not Found");
            callbackWithUser.onError(new HttpStatus().getNotFound());
        }
    }

    public void update(User newUser, CallbackWithUser callbackWithUser) {
        try {
            final int rowNum = updateUsers(newUser);
            if (rowNum == 0) {
                log.error("Bad Request");
                callbackWithUser.onError(new HttpStatus().getBadRequest());
                return;
            }
            callbackWithUser.onSuccess(new HttpStatus().getOk(), newUser);

        } catch (Exception e) {
            log.error("Bad Request");
            callbackWithUser.onError(new HttpStatus().getBadRequest());
        }
    }

    public void updateInfo(UsersData usersData, CallbackWithUser callbackWithUser) {
        try {
            System.out.println(usersData.getJson());
            final int rownum = jdbcTemplate.update(
                    "UPDATE usersData SET rating = ?, game_count = ?, game_count_win = ?, " +
                            "crystal_purple = ?, crystal_red = ?, crystal_blue = ?, crystal_green = ?" +
                            "WHERE login = ?",
                    usersData.getRating(),
                    usersData.getGameCount(),
                    usersData.getGameCountWin(),
                    usersData.getCrystalPurple(),
                    usersData.getCrystalRed(),
                    usersData.getCrystalBlue(),
                    usersData.getCrystalGreen(),
                    usersData.getLogin()
            );
            if (rownum == 0) {
                log.error("Bad Request");
                callbackWithUser.onError(new HttpStatus().getBadRequest());
            } else {
                callbackWithUser.onSuccess(new HttpStatus().getOk(), usersData);
            }
        } catch (Exception e) {
            log.error("Bad Request");
            callbackWithUser.onError(new HttpStatus().getBadRequest());
        }
    }

    public void changePass(User user, CallbackWithUser callbackWithUser) {

        try {
            if (!checkINputPasAndLog(user)) {
                callbackWithUser.onError(new HttpStatus().getNotFound());
                return;
            }
        } catch (Exception e) {
            log.error("Not Found");
            callbackWithUser.onError(new HttpStatus().getNotFound());
            return;
        }
        try {
            final String SQL = "UPDATE users SET password= ? where login=?";
            final int rownum = jdbcTemplate.update(
                    SQL, user.getNewHashPassword(), user.getLogin());

            if (rownum == 0) {
                log.error("Bad Request");
                callbackWithUser.onError(new HttpStatus().getBadRequest());
            } else {
                callbackWithUser.onSuccess(new HttpStatus().getOk(), user);
            }
        } catch (Exception e) {
            log.error("Bad Request");
            callbackWithUser.onError(new HttpStatus().getBadRequest());
        }
    }

    public JSONArray getLeaders() {
        final JSONArray jsonArray = new JSONArray();
        final String SQL = "SELECT login, rating FROM usersdata ORDER BY rating DESC LIMIT 20";
        final List<UsersData> users = jdbcTemplate.query(SQL, (rs, rownumber) -> {
            final UsersData usersData = new UsersData();
            usersData.setLogin(rs.getString("login"));
            usersData.setRating(rs.getInt("rating"));
            return usersData;
        });
        for (UsersData user : users) {
            jsonArray.put(user.getJson());
        }
        return jsonArray;
    }
}