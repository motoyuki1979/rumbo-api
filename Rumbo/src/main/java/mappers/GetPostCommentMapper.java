package mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import models.GetComments;

public class GetPostCommentMapper implements RowMapper<GetComments> {

	@Override
	public GetComments mapRow(ResultSet rs, int rowNum) throws SQLException {
		GetComments comments = new GetComments();
		
		comments.setComment_id(rs.getString("comment_id"));
		comments.setUser_id(rs.getString("comment_user_id"));
		comments.setUser_name("");
		comments.setUser_image("");
		comments.setIs_like("");
		comments.setPost_id(rs.getString("post_id"));
		comments.setDate(rs.getString("date"));
		comments.setLikes("0");
		comments.setComment(rs.getString("comment"));
		
		return comments;
	}

}
