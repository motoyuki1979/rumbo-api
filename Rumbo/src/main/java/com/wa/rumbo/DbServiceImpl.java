package com.wa.rumbo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import mappers.CategoryMapper;
import mappers.CmtIsLikeMapper;
import mappers.GetCategoryCommentMapper;
import mappers.GetNotificationsMapper;
import mappers.GetPostCommentMapper;
import mappers.GetPostMapper;
import mappers.IsLikeMapper;
import mappers.PostLikeMapper;
import mappers.UserMapper;
import models.AddPost;
import models.Categories;
import models.GetComments;
import models.GetNotifications;
import models.GetPost;
import models.PostComment;
import models.PostLike;
import models.User;

@Repository("DbServices")

public class DbServiceImpl implements DbServices {

	JdbcTemplate jdbcTemplate;

	@Autowired
	public DbServiceImpl(DataSource dataSource) {
		// TODO Auto-generated constructor stub
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public int insertUser(String device_id, String device_token) {
		String token = UUID.randomUUID().toString();
		String sql = "INSERT INTO `users`(`device_id`, `token`,`device_token`) VALUES (?,?,?)";
		int rs = jdbcTemplate.update(sql, device_id, token, device_token);
		return rs;
	}

	@Override
	public User checkUser(String device_id) {
		String sql = "select * from users where device_id='" + device_id + "' ";
		List<User> user = jdbcTemplate.query(sql, new UserMapper());
		if (user.size() > 0) {
			return user.get(0);
		} else {
			return null;
		}
	}

	@Override
	public boolean isHeaderValid(String authorization, String id) {

		String qry = "select * from users where user_id='" + id + "' and token='" + authorization + "' ";
		List<User> user = jdbcTemplate.query(qry, new UserMapper());
		if (user.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Categories> getCategories() {
		String qry = "select * from categories";
		List<Categories> categories = jdbcTemplate.query(qry, new CategoryMapper());

		for (int i = 0; i < categories.size(); i++) {
			String qry1 = "SELECT * FROM `category_comments` where category_id='" + categories.get(i).getCategory_id()
					+ "' ";// ORDER by id DESC
			List<GetComments> list = jdbcTemplate.query(qry1, new GetCategoryCommentMapper());
			if (list.size() > 0) {
				categories.get(i).setTotal_comments(""+list.size());
				categories.get(i).setLast_comment(list.get(0));
			}
		}

		return categories;
	}

	@Override
	public int addPost(final AddPost addPost, final String user_id) {
		String token = UUID.randomUUID().toString();
		final String[] ary = token.split("-");
		final String sql = "INSERT INTO post(post_id,post_user_id,date,title,expenditure,todays_tweets,category_id,category_name) VALUES (?,?,N?,N?,N?,N?,?,N?)";
		// final String sql = "INSERT INTO
		// post(post_id,post_user_id,date,title,expenditure,todays_tweets,category_id,category_name)"
		// + " VALUES ('" + ary[0] + "','" + user_id + "',N'" + addPost.getDate() +
		// "',N'" + addPost.getTitle()
		// + "',N'" + addPost.getExpenditure() + "',N'" + addPost.getTodays_tweets() +
		// "','"
		// + addPost.getCategory_id() + "',N'" + addPost.getCategory_name() + "')";
		// int rss = jdbcTemplate.update(sql);

		final KeyHolder keyHolder = new GeneratedKeyHolder();

		int rs = jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				// TODO Auto-generated method stub
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, ary[0]);
				ps.setString(2, user_id);
				ps.setString(3, addPost.getDate());
				ps.setString(4, addPost.getTitle());
				ps.setString(5, addPost.getExpenditure());
				ps.setString(6, addPost.getTodays_tweets());
				ps.setString(7, addPost.getCategory_id());
				ps.setString(8, addPost.getCategory_name());
				return ps;
			}
		}, keyHolder);

		long id = (Long) keyHolder.getKey();

		return rs;
	}

	@Override
	public List<GetPost> getPosts(String user_id) {
		String qry = "SELECT * FROM `post` ORDER by id DESC";
		List<GetPost> list = jdbcTemplate.query(qry, new GetPostMapper());

		for (int i = 0; i < list.size(); i++) {

			String qry1 = "SELECT * FROM users where user_id='" + list.get(i).getUser_id() + "' ";
			List<User> user = jdbcTemplate.query(qry1, new UserMapper());
			if (user.size() > 0) {
				list.get(i).setUser_name(user.get(0).getUser_name());
				list.get(i).setUser_image(user.get(0).getImage());
			}

			String qry_islike = "select like_user_id from post_likes where post_id='" + list.get(i).getPost_id()
					+ "' and like_user_id='" + user_id + "'";
			List<IsLikeMapper.MyLike> myLikeList = jdbcTemplate.query(qry_islike, new IsLikeMapper());
			if (myLikeList.size() > 0) {
				list.get(i).setIs_like("true");
			} else {
				list.get(i).setIs_like("false");
			}

			String likesCountQuery = "SELECT * from post_likes where post_id='" + list.get(i).getPost_id() + "'";
			List<PostLike> likes_count_list = jdbcTemplate.query(likesCountQuery, new PostLikeMapper());
			list.get(i).setLikes_count("" + likes_count_list.size());

			String comments_count_query = "SELECT * from post_comments where post_id='" + list.get(i).getPost_id()
					+ "'";
			List<GetComments> comment_list = jdbcTemplate.query(comments_count_query, new GetPostCommentMapper());
			list.get(i).setComments_count("" + comment_list.size());
		}

		return list;
	}

	@Override
	public int postComment(PostComment postCmt, String user_id) {

		final String sql = "INSERT INTO post_comments(post_id,comment_user_id,date,comment)" + " VALUES ('"
				+ postCmt.getPost_id() + "','" + user_id + "','" + System.currentTimeMillis() + "',N'"
				+ postCmt.getComment() + "')";
		int rs = jdbcTemplate.update(sql);

		// String qry = "SELECT * FROM `post_comments` where post_id='" +
		// postCmt.getPost_id() + "' ";// ORDER by id DESC
		// List<GetComments> list = jdbcTemplate.query(qry, new GetPostCommentMapper());
		return rs;
	}

	@Override
	public List<GetComments> getPostComments(String post_id, String user_id) {
		String qry = "SELECT * FROM `post_comments` where post_id='" + post_id + "' ";// ORDER by id DESC
		List<GetComments> list = jdbcTemplate.query(qry, new GetPostCommentMapper());

		for (int i = 0; i < list.size(); i++) {
			String qry_islike = "select comment_id from comment_like where comment_id='" + list.get(i).getComment_id()
					+ "' and like_user_id='" + user_id + "'";
			List<CmtIsLikeMapper.IsLike> myLikeList = jdbcTemplate.query(qry_islike, new CmtIsLikeMapper());
			if (myLikeList.size() > 0) {
				list.get(i).setIs_like("true");
			} else {
				list.get(i).setIs_like("false");
			}

			/*
			 * String likesCountQuery = "SELECT * from comment_like where comment_id='" +
			 * list.get(i).getComment_id()+"'"; List<CmtIsLikeMapper.IsLike>
			 * likes_count_list = jdbcTemplate.query(qry_islike, new CmtIsLikeMapper());
			 * list.get(i).setLikes(""+likes_count_list.size());
			 */
			String likesCountQuery = "SELECT count(id) from comment_like where comment_id='"
					+ list.get(i).getComment_id() + "'";
			int likes_count_list = jdbcTemplate.queryForInt(likesCountQuery);
			list.get(i).setLikes("" + likes_count_list);

			String userQuery = "SELECT * from users where user_id='" + list.get(i).getUser_id() + "'";
			List<User> user = jdbcTemplate.query(userQuery, new UserMapper());
			list.get(i).setUser_name(user.get(0).getUser_name());

		}
		return list;
	}

	@Override
	public int likePost(String post_id, String user_id) {
		String sql1 = "SELECT * from post_likes where post_id='" + post_id + "' and like_user_id='" + user_id + "' ";
		List<PostLike> list = jdbcTemplate.query(sql1, new PostLikeMapper());
		if (list.size() > 0) {
			String id = list.get(0).getId();
			String sql = "delete from post_likes where id='" + id + "' ";
			int rs = jdbcTemplate.update(sql);
			if (rs > 0) {
				rs = 2;
			}
			return rs;
		} else {
			String sql = "INSERT INTO post_likes(post_id,like_user_id)" + " VALUES ('" + post_id + "','" + user_id
					+ "')";
			int rs = jdbcTemplate.update(sql);

			return rs;
		}

	}

	@Override
	public int likeComment(String comment_id, String user_id, String post_id) {
		String sql1 = "SELECT * from comment_like where comment_id='" + comment_id + "' and like_user_id='" + user_id
				+ "' ";
		List<PostLike> list = jdbcTemplate.query(sql1, new PostLikeMapper());
		if (list.size() > 0) {
			String id = list.get(0).getId();
			String sql = "delete from comment_like where id='" + id + "' ";
			int rs = jdbcTemplate.update(sql);
			if (rs > 0) {
				rs = 2;
			}
			return rs;
		} else {
			String sql = "INSERT INTO comment_like(comment_id,like_user_id)" + " VALUES ('" + comment_id + "','"
					+ user_id + "')";
			int rs = jdbcTemplate.update(sql);

			/* Get user_id whose comment liked by user to send him notification */
			/*
			 * String qry = "SELECT * FROM `post_comments` where post_id='" + post_id +
			 * "' ";// ORDER by id DESC List<GetComments> post_comments =
			 * jdbcTemplate.query(qry, new GetPostCommentMapper()); String to_user_id =
			 * post_comments.get(0).getUser_id();
			 * 
			 * String qry2 = "SELECT * FROM `users` where user_id='" + user_id + "' ";//
			 * ORDER by id DESC List<User> user = jdbcTemplate.query(qry2, new
			 * UserMapper()); String username = user.get(0).getUser_name();
			 * 
			 * String message = username + " likes your comment"; String sql3 =
			 * "INSERT INTO notifications(comment_id,from_user_id,to_user_id,message,datetime)"
			 * + " VALUES ('" + comment_id + "','" + user_id + "','" + to_user_id + "','" +
			 * message + "','" + System.currentTimeMillis() + "')"; int rs3 =
			 * jdbcTemplate.update(sql3);
			 */
			return rs;
		}
	}

	@Override
	public String getDeviceTokenByPostID(String post_id) {

		String qry = "SELECT * FROM `post_comments` where post_id='" + post_id + "' ";// ORDER by id DESC
		List<GetComments> post_comments = jdbcTemplate.query(qry, new GetPostCommentMapper());
		String to_user_id = post_comments.get(0).getUser_id();

		String qry2 = "SELECT * FROM `users` where user_id='" + to_user_id + "' ";// ORDER by id DESC
		List<User> user = jdbcTemplate.query(qry2, new UserMapper());
		String device_token = user.get(0).getDevice_token();

		return device_token;
	}

	@Override
	public String insertNotificationData(String comment_id, String user_id, String post_id) {
		String qry = "SELECT * FROM `post_comments` where post_id='" + post_id + "' ";// ORDER by id DESC
		List<GetComments> post_comments = jdbcTemplate.query(qry, new GetPostCommentMapper());
		String to_user_id = post_comments.get(0).getUser_id();

		String qry2 = "SELECT * FROM `users` where user_id='" + user_id + "' ";// ORDER by id DESC
		List<User> user = jdbcTemplate.query(qry2, new UserMapper());
		String username = user.get(0).getUser_name();

		String message = username + " likes your comment";
		String sql3 = "INSERT INTO notifications(comment_id,from_user_id,to_user_id,message,datetime)" + " VALUES ('"
				+ comment_id + "','" + user_id + "','" + to_user_id + "','" + message + "','"
				+ System.currentTimeMillis() + "')";
		int rs3 = jdbcTemplate.update(sql3);

		return message;
	}

	@Override
	public List<GetNotifications> getNotificationList(String user_id) {
		String qry = "SELECT * FROM `notifications` where to_user_id='" + user_id + "' ";
		List<GetNotifications> notificationList = jdbcTemplate.query(qry, new GetNotificationsMapper());
		return notificationList;
	}

	@Override
	public GetPost getPostDetails(String post_id, String user_id) {
		String qry = "SELECT * FROM `post` ORDER by id DESC";
		List<GetPost> list = jdbcTemplate.query(qry, new GetPostMapper());

		for (int i = 0; i < list.size(); i++) {

			String qry1 = "SELECT * FROM users where user_id='" + list.get(i).getUser_id() + "' ";
			List<User> user = jdbcTemplate.query(qry1, new UserMapper());
			if (user.size() > 0) {
				list.get(i).setUser_name(user.get(0).getUser_name());
				list.get(i).setUser_image(user.get(0).getImage());
			}

			String qry_islike = "select like_user_id from post_likes where post_id='" + post_id + "' and like_user_id='"
					+ user_id + "'";
			List<IsLikeMapper.MyLike> myLikeList = jdbcTemplate.query(qry_islike, new IsLikeMapper());
			if (myLikeList.size() > 0) {
				list.get(i).setIs_like("true");
			} else {
				list.get(i).setIs_like("false");
			}

			String likesCountQuery = "SELECT * from post_likes where post_id='" + post_id + "'";
			List<PostLike> likes_count_list = jdbcTemplate.query(likesCountQuery, new PostLikeMapper());
			list.get(i).setLikes_count("" + likes_count_list.size());

			String comments_count_query = "SELECT * from post_comments where post_id='" + post_id + "'";
			List<GetComments> comment_list = jdbcTemplate.query(comments_count_query, new GetPostCommentMapper());
			list.get(i).setComments_count("" + comment_list.size());
		}

		return list.get(0);
	}

	@Override
	public User getUserProfile(String user_id) {
		String sql = "select * from users where user_id='" + user_id + "' ";
		List<User> user = jdbcTemplate.query(sql, new UserMapper());
		if (user.size() > 0) {
			return user.get(0);
		} else {
			return null;
		}
	}

}
