package models;

public class PostLike {

	String id;
	String post_id;
	String like_user_id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPost_id() {
		return post_id;
	}

	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}

	public String getLike_user_id() {
		return like_user_id;
	}

	public void setLike_user_id(String like_user_id) {
		this.like_user_id = like_user_id;
	}

}
