package mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CmtIsLikeMapper implements RowMapper<CmtIsLikeMapper.IsLike> {

	public class IsLike {
		String id;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

	}

	@Override
	public IsLike mapRow(ResultSet rs, int rowNum) throws SQLException {
		IsLike isLike = new IsLike();
		isLike.setId(rs.getString("comment_id"));
		return isLike;
	}
}
