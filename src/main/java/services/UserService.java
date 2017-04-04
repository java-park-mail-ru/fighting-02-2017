package services;

import objects.HttpStatus;
import objects.ObjUser;
import objects.ObjUsersData;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import services.mappers.UserMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;
/**
 * Created by andrey on 21.02.2017.
 */
@Service
public class UserService {
    final static Logger log = Logger.getLogger(UserService.class.getName());
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

    public void register(ObjUser objUser, Callback callback) {
        if (objUser.getPassword().isEmpty() || objUser.getLogin().isEmpty()) {
            callback.onError(new HttpStatus().getForbidden());
        } else if (objUser.getPassword().length() < 8) {
            log.error("Forbidden");
            callback.onError(new HttpStatus().getForbidden());
        } else if (objUser.getLogin().matches("[а-яА-ЯёЁ]+")) {
            log.error("Forbidden");
            callback.onError(new HttpStatus().getForbidden());
        } else if (objUser.getLogin().length() < 4) {
            log.error("Forbidden");
            callback.onError(new HttpStatus().getForbidden());
        } else {
            try {
                registerUser(objUser);
                callback.onSuccess(new HttpStatus().getOk());
            } catch (RuntimeException e) {
                log.error("Forbidden");
                callback.onError(new HttpStatus().getForbidden());
            }
        }
    }

    @Transactional
    private void registerUser(ObjUser objUser) {
        jdbcTemplate.update("insert into users (login,password) values (?,?);", objUser.getLogin(), objUser.getHashPassword());
        jdbcTemplate.update("INSERT INTO usersData (login) values (?);", objUser.getLogin());
    }

    @Transactional
    private int updateUsers(ObjUser objUser) {
        int rowNumb;
        rowNumb = jdbcTemplate.update("update users set login=? where login=?;", objUser.getNewlogin(), objUser.getLogin());
        rowNumb += jdbcTemplate.update("update usersdata set login=? where login=?;", objUser.getNewlogin(), objUser.getLogin());
        return rowNumb;
    }

    @Transactional
    private boolean checkINputPasAndLog(ObjUser user) {
        String SQL = "SELECT * FROM users WHERE login = ?";
        ObjUser userDB = jdbcTemplate.queryForObject(SQL,
                new Object[]{user.getLogin()}, new UserMapper());
        return user.comparePass.test(userDB.getPassword());
    }

    public void login(ObjUser objUser, CallbackWithUser callbackWithUser) {
        try {
            if (checkINputPasAndLog(objUser)) callbackWithUser.onSuccess(new HttpStatus().getOk(), objUser);
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
            String SQL = "SELECT * FROM users WHERE login = ?";
            ObjUser userDB = jdbcTemplate.queryForObject(SQL,
                    new Object[]{login}, new UserMapper());
            callbackWithUser.onSuccess(new HttpStatus().getOk(), userDB);
        }
        catch (Exception e){
            log.error("Not Found");
            callbackWithUser.onError(new HttpStatus().getNotFound());
        }
    }

    public void update(ObjUser newObjUser, CallbackWithUser callbackWithUser) {
        try {
            int rowNum = updateUsers(newObjUser);
            if (rowNum == 0) throw new Exception();
            callbackWithUser.onSuccess(new HttpStatus().getOk(), newObjUser);

        } catch (Exception e) {
            log.error("Bad Request");
            callbackWithUser.onError(new HttpStatus().getBadRequest());
        }
    }

    public void updateInfo(ObjUsersData objUsersData, CallbackWithUser callbackWithUser) {
        try {
            System.out.println(objUsersData.getJson());
            int rownum = jdbcTemplate.update(
                    "UPDATE usersData SET rating = ?, game_count = ?, game_count_win = ?, " +
                            "crystal_purple = ?, crystal_red = ?, crystal_blue = ?, crystal_green = ?" +
                            "WHERE login = ?",
                    objUsersData.getRating(),
                    objUsersData.getGameCount(),
                    objUsersData.getGameCountWin(),
                    objUsersData.getCrystalPurple(),
                    objUsersData.getCrystalRed(),
                    objUsersData.getCrystalBlue(),
                    objUsersData.getCrystalGreen(),
                    objUsersData.getLogin()
            );
            if (rownum == 0) {
                log.error("Bad Request");
                callbackWithUser.onError(new HttpStatus().getBadRequest());
            } else {
                callbackWithUser.onSuccess(new HttpStatus().getOk(), objUsersData);
            }
        } catch (Exception e) {
            log.error("Bad Request");
            callbackWithUser.onError(new HttpStatus().getBadRequest());
        }
    }

    public void changePass(ObjUser objUser, CallbackWithUser callbackWithUser) {

        try {
            if (!checkINputPasAndLog(objUser)) {
                callbackWithUser.onError(new HttpStatus().getNotFound());
                return;
            }
        } catch (Exception e) {
            log.error("Not Found");
            callbackWithUser.onError(new HttpStatus().getNotFound());
            return;
        }
        String SQL = "UPDATE users SET password= ? where login=?";
        try {
            int rownum = jdbcTemplate.update(
                    SQL, objUser.getNewHashPassword(), objUser.getLogin());

            if (rownum == 0) {
                log.error("Bad Request");
                callbackWithUser.onError(new HttpStatus().getBadRequest());
            } else {
                callbackWithUser.onSuccess(new HttpStatus().getOk(), objUser);
            }
        } catch (Exception e) {
            log.error("Bad Request");
            callbackWithUser.onError(new HttpStatus().getBadRequest());
        }
    }

    public JSONArray getLeaders() {
        final JSONArray jsonArray = new JSONArray();
        String SQL = "SELECT login, rating FROM usersdata ORDER BY rating DESC LIMIT 20";
        List<ObjUsersData> users = jdbcTemplate.query(SQL, (rs, rownumber) -> {
            ObjUsersData objUsersData = new ObjUsersData();
            objUsersData.setLogin(rs.getString("login"));
            objUsersData.setRating(rs.getInt("rating"));
            return objUsersData;
        });
        for (ObjUsersData user : users) {
            jsonArray.put(user.getJson());
        }
        return jsonArray;
    }
}