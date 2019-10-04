package mappers;

import models.PostLike;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PostLikeMapper implements RowMapper<PostLike> {

	@Override
	public PostLike mapRow(ResultSet rs, int rowNum) throws SQLException {
		PostLike postLike = new PostLike();
		postLike.setId(rs.getString("id"));
		//postLike.setPost_id(rs.getString("post_id"));
		postLike.setLike_user_id(rs.getString("like_user_id"));

		return postLike;
	}

}
