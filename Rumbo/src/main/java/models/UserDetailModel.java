/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.List;

/**
 *
 * @author webastral
 */
public class UserDetailModel {

    String success;
    String message;
    User user;
    List<GetPost> user_posts;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<GetPost> getUser_posts() {
        return user_posts;
    }

    public void setUser_posts(List<GetPost> user_posts) {
        this.user_posts = user_posts;
    }

    
}
