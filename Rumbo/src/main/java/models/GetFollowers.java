/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author webastral
 */
public class GetFollowers {
    String user_id;
    String follower_id;
    String follower_username;
    String follower_image;
    String follower_info;

    public String getFollower_info() {
        return follower_info;
    }

    public void setFollower_info(String follower_info) {
        this.follower_info = follower_info;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(String follower_id) {
        this.follower_id = follower_id;
    }

    public String getFollower_username() {
        return follower_username;
    }

    public void setFollower_username(String follower_username) {
        this.follower_username = follower_username;
    }

    public String getFollower_image() {
        return follower_image;
    }

    public void setFollower_image(String follower_image) {
        this.follower_image = follower_image;
    }
    
}
