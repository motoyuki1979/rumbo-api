package models;

import org.codehaus.jackson.annotate.JsonProperty;

public class PostComment {

	@JsonProperty("post_id")
	String post_id;
	@JsonProperty("comment")
	String comment;

	public String getPost_id() {
		return post_id;
	}

	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
