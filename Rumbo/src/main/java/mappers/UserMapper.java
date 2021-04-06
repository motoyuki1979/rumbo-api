package mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import models.User;

public class UserMapper implements RowMapper<User>  {

  /*public class Username {
        String user_name;
	
        public String getId() {
            return user_name;
	}

	public void setId(String id) {
            this.user_name = id;
	}
    }
    
        @Override
	public UserMapper.Username mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserMapper.Username username = new UserMapper.Username();
		username.setId(rs.getString("user_name"));
		return username;
	}*/
        
	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();
		user.setUser_id(rs.getString("user_id"));
		// user.setDevice_id(rs.getString("device_id"));
		user.setToken(rs.getString("token"));
		user.setAddress(rs.getString("address"));
		user.setUser_name(rs.getString("user_name"));
		user.setImage(rs.getString("image"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setIntroduction(rs.getString("introduction"));
                user.setFollowing_count(rs.getString("following_count"));
                user.setFollower_count(rs.getString("follower_count"));
		return user;
	}

}
