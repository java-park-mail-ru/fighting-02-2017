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
    private static final Logger log = Logger.getLogger(UserService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String register(User user) {
        if (user.getPassword().isEmpty() || user.getLogin().isEmpty()
                || (user.getPassword().length() < 8) || (user.getLogin().matches("[а-яА-ЯёЁ]+"))
                || (user.getLogin().length() < 4)) {
            log.error("Forbidden");
            return new HttpStatus().getForbidden();
        }
            try {
                registerUser(user);
                return new HttpStatus().getOk();
            } catch (RuntimeException e) {
                log.error("Forbidden");
                return new HttpStatus().getForbidden();
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

    public Object getUser(String login) {
        try {
            final String SQL = "SELECT * FROM users WHERE login = ?";
            final User userDB = jdbcTemplate.queryForObject(SQL,
                    new Object[]{login}, new UserMapper());
            return userDB;
        } catch (DataAccessException e) {
            log.error("Not Found");
            return new HttpStatus().getNotFound();
        }
    }

    public String login(User user) {
        try {
            if (checkINputPasAndLog(user)) return new HttpStatus().getOk();
            else {
                log.error("Not Found");
                return new HttpStatus().getNotFound();
            }
        } catch (Exception e) {
            log.error("Not Found");
            return new HttpStatus().getNotFound();
        }
    }

    public Object update(User newUser) {
        try {
            final int rowNum = updateUsers(newUser);
            if (rowNum == 0) {
                log.error("Bad Request");
                return new HttpStatus().getBadRequest();
            }
            return newUser;
        } catch (Exception e) {
            log.error("Bad Request");
            return new HttpStatus().getBadRequest();
        }
    }

    public Object updateInfo(UsersData usersData) {
        try {
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
                return new HttpStatus().getBadRequest();
            }
            else return usersData;
        } catch (Exception e) {
            log.error("Bad Request");
           return new HttpStatus().getBadRequest();
        }
    }

    public String changePass(User user) {
        try {
            if (!checkINputPasAndLog(user)) {
                return new HttpStatus().getNotFound();
            }
        } catch (Exception e) {
            log.error("Not Found");
            return new HttpStatus().getNotFound();
        }
        try {
            final String SQL = "UPDATE users SET password= ? where login=?";
            final int rownum = jdbcTemplate.update(
                    SQL, user.getNewHashPassword(), user.getLogin());

            if (rownum == 0) {
                log.error("Bad Request");
                return new HttpStatus().getBadRequest();
            } else {
                return new HttpStatus().getOk();
            }
        } catch (Exception e) {
            log.error("Bad Request");
            return new HttpStatus().getBadRequest();
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