/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import models.ComunityCommentLike;
import models.PostLike;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author webastral
 */
public class ComunityCommentsLikeMapper implements RowMapper<ComunityCommentLike> {

	@Override
	public ComunityCommentLike mapRow(ResultSet rs, int rowNum) throws SQLException {
		ComunityCommentLike commentLike = new ComunityCommentLike();
		commentLike.setUser_id(rs.getString("user_id"));
		commentLike.setComment_id(rs.getString("comment_id"));

		return commentLike;
	}
    
}
