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
