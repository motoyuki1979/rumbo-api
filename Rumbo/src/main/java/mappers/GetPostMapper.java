package mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import models.GetPost;

public class GetPostMapper implements RowMapper<GetPost>{

	@Override
	public GetPost mapRow(ResultSet rs, int rowNum) throws SQLException {
		GetPost getPost = new GetPost();
		
		getPost.setId(rs.getString("id"));
		getPost.setUser_id(rs.getString("post_user_id"));
		getPost.setUser_name("");
		getPost.setUser_image("");
		getPost.setIs_like("");
		getPost.setPost_id(rs.getString("post_id"));
		getPost.setDate(rs.getString("date"));
		getPost.setTitle(rs.getString("title"));
		getPost.setExpenditure(rs.getString("expenditure"));
		getPost.setTodays_tweets(rs.getString("todays_tweets"));
		getPost.setCategory_id(rs.getString("category_id"));
		getPost.setCategory_name(rs.getString("category_name"));
		
		return getPost;
	}

}
