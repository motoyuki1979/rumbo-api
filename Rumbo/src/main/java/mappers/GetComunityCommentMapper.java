package mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import models.GetComments;
import models.GetComunityComments;
import org.springframework.jdbc.core.RowMapper;


public class GetComunityCommentMapper implements RowMapper<GetComunityComments> {

	@Override
	public GetComunityComments mapRow(ResultSet rs, int rowNum) throws SQLException {
		GetComunityComments comments = new GetComunityComments();
		
		comments.setComment_id(rs.getString("comment_id"));
	        comments.setComment_user_id(rs.getString("comment_user_id"));
		comments.setCategory_id(rs.getString("category_id"));
		comments.setComment(rs.getString("comment"));
		comments.setDate(rs.getString("date"));
//                comments.setIs_like(rs.getString("is_like"));
//                comments.setLikes_count(rs.getString("likes_count"));
		
		return comments;
	}
    
}
