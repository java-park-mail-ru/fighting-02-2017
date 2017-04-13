package services.mappers;

import objects.UsersData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by andrey on 27.03.17.
 */
public class UsersDataMapper implements RowMapper<UsersData> {
    @Override
    public UsersData mapRow(ResultSet rs, int rowNum) throws SQLException {
        final UsersData usersData = new UsersData();
        usersData.setLogin(rs.getString("login"));
        usersData.setGameCount(rs.getInt("game_count"));
        usersData.setGameCountWin(rs.getInt("game_count_win"));
        usersData.setCrystalGreen(rs.getInt("crystal_green"));
        usersData.setCrystalRed(rs.getInt("crystal_red"));
        usersData.setCrystalBlue(rs.getInt("crystal_blue"));
        usersData.setCrystalPurple(rs.getInt("crystal_purple"));
        usersData.setRating(rs.getInt("rating"));
        return usersData;
    }
}
