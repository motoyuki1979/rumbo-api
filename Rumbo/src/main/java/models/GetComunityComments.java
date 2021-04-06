package models;

public class GetComunityComments {
    String comment_id;
	String comment_user_id;
	String category_id;
	String comment;
	String date;
	String is_like;
        String likes_count;
        String user_name;
        String user_image;

    public String getIs_like() {
        return is_like;
    }

    public void setIs_like(String is_like) {
        this.is_like = is_like;
    }

    public String getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(String likes_count) {
        this.likes_count = likes_count;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

	public String getComment_id() {
		return comment_id;
	}

	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}

	public String getComment_user_id() {
		return comment_user_id;
	}

	public void setComment_user_id(String comment_user_id) {
		this.comment_user_id = comment_user_id;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

        public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

         public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

       
}

