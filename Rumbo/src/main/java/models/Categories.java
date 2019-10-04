package models;

public class Categories {

	String category_id;
	String category_name;
	String category_image;
	GetComments last_comment;
	String total_comments;

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

	public GetComments getLast_comment() {
		return last_comment;
	}

	public void setLast_comment(GetComments last_comment) {
		this.last_comment = last_comment;
	}

	public String getTotal_comments() {
		return total_comments;
	}

	public void setTotal_comments(String total_comments) {
		this.total_comments = total_comments;
	}

    
        
}
