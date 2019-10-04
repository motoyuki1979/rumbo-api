package mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class IsLikeMapper implements RowMapper<IsLikeMapper.MyLike> {

	public class MyLike {
		String id;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

	}

	@Override
	public IsLikeMapper.MyLike mapRow(ResultSet rs, int rowNum) throws SQLException {
		MyLike myLike = new MyLike();
		myLike.setId(rs.getString("like_user_id"));
		
		return myLike;
	}
}
