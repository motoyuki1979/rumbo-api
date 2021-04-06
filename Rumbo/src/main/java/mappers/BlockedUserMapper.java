/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import models.ComunityCommentLike;
import models.GetBlockedUser;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author webastral
 */
public class BlockedUserMapper implements RowMapper<GetBlockedUser> {

	@Override
	public GetBlockedUser mapRow(ResultSet rs, int rowNum) throws SQLException {
		GetBlockedUser getBlockedUser = new GetBlockedUser();
		getBlockedUser.setUser_id(rs.getString("user_id"));
		getBlockedUser.setBlocked_user_id(rs.getString("blocked_user_id"));

		return getBlockedUser;
	}
    
}
