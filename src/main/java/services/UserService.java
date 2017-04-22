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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by andrey on 21.02.2017.
 */
@Service
public class UserService {
    public UserService(){}
    private static final Logger log = Logger.getLogger(UserService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public String register(User user) throws Exception {
        if (user.getPassword().isEmpty() || user.getLogin().isEmpty()
                || (user.getPassword().length() < 8) || (user.getLogin().matches("[а-яА-ЯёЁ]+"))
                || (user.getLogin().length() < 4)) {
            throw new Exception();
        }
        registerUser(user);
        return new HttpStatus().getOk();

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
        if (userDB==null) return false;
        return user.comparePass.test(userDB.getPassword());
    }

    public User getUser(String login) throws Exception{
            final String SQL = "SELECT * FROM users WHERE login = ?";
            final User userDB = jdbcTemplate.queryForObject(SQL, new Object[]{login}, new UserMapper());
            return userDB;
    }

    public String login(User user) throws Exception {
        //try {
            if (checkINputPasAndLog(user)) return new HttpStatus().getOk();
            else {
                log.error("Not Found");
                throw new Exception();
            }
    }

    public User update(User newUser) throws Exception {
            final int rowNum = updateUsers(newUser);
            if (rowNum == 0) {
                log.error("Bad Request");
                throw  new Exception();
            }
            return newUser;
    }

    public UsersData updateInfo(UsersData usersData) throws Exception {
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
                throw new Exception();
            }
             return usersData;
    }

    public String changePass(User user) throws Exception {
            if (!checkINputPasAndLog(user)) {
                log.error("Bad Request");
                throw new Exception();
            }

            final String SQL = "UPDATE users SET password= ? where login=?";
            final int rownum = jdbcTemplate.update(
                    SQL, user.getNewHashPassword(), user.getLogin());
            if (rownum == 0) {
                log.error("Bad Request");
                throw new Exception();
            }
            return new HttpStatus().getOk();
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