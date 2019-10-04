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
public class PostDetailModel {

    String success;
    String message;
    GetPost post;
    List<GetComments> post_comments;

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

    public GetPost getPost() {
        return post;
    }

    public void setPost(GetPost post) {
        this.post = post;
    }

    public List<GetComments> getPost_comments() {
        return post_comments;
    }

    public void setPost_comments(List<GetComments> post_comments) {
        this.post_comments = post_comments;
    }

    
}
