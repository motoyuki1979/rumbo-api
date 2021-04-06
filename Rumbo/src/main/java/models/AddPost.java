package models;

import org.codehaus.jackson.annotate.JsonProperty;

public class AddPost {

	@JsonProperty("date")
	String date;
	@JsonProperty("title")
	String title;
	@JsonProperty("expenditure")
	String expenditure;
	@JsonProperty("todays_tweets")
	String todays_tweets;
	@JsonProperty("category_id")
	String category_id;
	@JsonProperty("category_name")
	String category_name;
        @JsonProperty("category_image")
	String category_image;
         @JsonProperty("post_category")
	String post_category;

    public String getPost_category() {
        return post_category;
    }

    public void setPost_category(String post_category) {
        this.post_category = post_category;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getExpenditure() {
		return expenditure;
	}

	public void setExpenditure(String expenditure) {
		this.expenditure = expenditure;
	}

	public String getTodays_tweets() {
		return todays_tweets;
	}

	public void setTodays_tweets(String todays_tweets) {
		this.todays_tweets = todays_tweets;
	}

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

}
