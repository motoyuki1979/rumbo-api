package com.wa.rumbo;

import java.util.List;

import models.AddPost;
import models.Categories;
import models.GetComments;
import models.GetNotifications;
import models.GetPost;
import models.PostComment;
import models.User;

public interface DbServices {

	public int insertUser(String device_id, String device_token);

	public User checkUser(String device_id);

	public boolean isHeaderValid(String authorization, String id);

	public List<Categories> getCategories();

	public int addPost(AddPost addPost, String user_id);

	public List<GetPost> getPosts(String user_id);

	public int postComment(PostComment postComment, String user_id);

	public List<GetComments> getPostComments(String post_id, String user_id);

	public int likePost(String post_id, String user_id);
	
	public int likeComment(String comment_id, String user_id,String post_id);

        public String insertNotificationData(String comment_id, String user_id,String post_id);

        public String getDeviceTokenByPostID(String post_id);

        public List<GetNotifications> getNotificationList(String user_id);

        public GetPost getPostDetails(String post_id,String user_id);

        public User getUserProfile(String user_id);

}
