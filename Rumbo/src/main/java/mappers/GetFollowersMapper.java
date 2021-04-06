/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import models.GetComunityComments;
import models.GetFollowers;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author webastral
 */
public class GetFollowersMapper implements RowMapper<GetFollowers> {

	@Override
	public GetFollowers mapRow(ResultSet rs, int rowNum) throws SQLException {
		GetFollowers followers = new GetFollowers();
		
		followers.setUser_id(rs.getString("user_id"));
	        followers.setFollower_id(rs.getString("follower_id"));
		followers.setFollower_username(rs.getString("follower_username"));
		followers.setFollower_image(rs.getString("follower_image"));
                followers.setFollower_info(rs.getString("follower_info"));
		
		return followers;
	}
    
}
