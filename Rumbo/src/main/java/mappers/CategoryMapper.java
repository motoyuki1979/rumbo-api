package mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import models.Categories;


public class CategoryMapper implements RowMapper<Categories> {

	@Override
	public Categories mapRow(ResultSet rs, int rowNum) throws SQLException {
		Categories categories = new Categories();
		categories.setCategory_id(rs.getString("category_id"));
		categories.setCategory_name(rs.getString("category_name"));
		categories.setCategory_image(rs.getString("category_image"));
		categories.setTotal_comments("");

		return categories;
	}

}
