package mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import models.GetComments;
import models.GetNotifications;

public class GetNotificationsMapper implements RowMapper<GetNotifications> {

	@Override
	public GetNotifications mapRow(ResultSet rs, int rowNum) throws SQLException {
		GetNotifications comments = new GetNotifications();
		
		comments.setId(rs.getString("id"));
		comments.setComment_id(rs.getString("comment_id"));
		comments.setFrom_user_id(rs.getString("from_user_id"));
		comments.setMessage(rs.getString("message"));
		comments.setDatetime(rs.getString("datetime"));
		comments.setPost_id(rs.getString("post_id"));
		comments.setType(rs.getString("type"));
		
		return comments;
	}

}
