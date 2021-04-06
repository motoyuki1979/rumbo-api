package com.wa.rumbo;

import java.util.List;
import models.AddCategoryCommentModel;

import models.AddPost;
import models.Categories;
import models.GetCalBooking;
import models.GetComments;
import models.GetComunityComments;
import models.GetFollowers;
import models.GetNotifications;
import models.GetPost;
import models.PostComment;
import models.User;

public interface DbServices {

	public int insertUser(String device_id, String device_token, String email, String password, String following_count, String follower_count);

	public User checkUser(String email);

	public boolean isHeaderValid(String authorization, String id);

	public List<Categories> getCategories();

	public int addPost(AddPost addPost, String user_id);

	public List<GetPost> getPosts(String user_id);

	public int postComment(PostComment postComment, String user_id);
        
        public int postCommentDelete(PostComment postComment, String user_id);
        
        public int addCategoryCommentData(AddCategoryCommentModel addCtgyComment, String user_id);

	public List<GetComments> getPostComments(String post_id, String user_id);

	public int likePost(String post_id, String user_id);
	
       	public int likeComynityComment(String comment_id, String user_id);

	public int likeComment(String comment_id, String user_id,String post_id);

        public String insertNotificationData(String comment_id, String user_id,String post_id);

        public String getDeviceTokenByPostID(String post_id);

        public List<GetNotifications> getNotificationList(String user_id);

        public GetPost getPostDetails(String post_id,String user_id);

        public List<User> getUserProfile(String user_id);

        public List<GetComunityComments> getComunityComments(String user_id);
        
        public int update_profile(String user_id, String user_name, String email,String password, String introduction, String image);
         
        public List<GetFollowers> getFollowersList(String follower_id);
        
        public List<GetFollowers> getFollowList(String user_id);
        
        public List<GetPost> getUserPosts(String user_id);
        
        public List<GetCalBooking> getCalenderBooking(String user_id);
        
        public int addFollowUser(String follower_id, String user_id);
        
        public int deleteFollowUser(String follower_id, String user_id);
        
        public int forgotPassword(String email);
}
