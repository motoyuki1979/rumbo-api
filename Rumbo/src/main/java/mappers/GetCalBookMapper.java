/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import models.GetCalBooking;
import models.GetPost;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author webastral
 */
public class GetCalBookMapper implements RowMapper<GetCalBooking>{

	@Override
	public GetCalBooking mapRow(ResultSet rs, int rowNum) throws SQLException {
		GetCalBooking getCalBooking = new GetCalBooking();
		
		getCalBooking.setTitle(rs.getString("title"));
                getCalBooking.setCategory_title(rs.getString("category_title"));
                getCalBooking.setCategory_image(rs.getString("category_image"));
                getCalBooking.setAmount(rs.getString("amount"));
                getCalBooking.setComment(rs.getString("comment"));
                getCalBooking.setDate(rs.getString("date"));
                getCalBooking.setUser_id(rs.getString("user_id"));
                getCalBooking.setPost_category(rs.getString("post_category"));
		
		return getCalBooking;
	}

}