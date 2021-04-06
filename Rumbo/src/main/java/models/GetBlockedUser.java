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
public class GetBlockedUser {
    String blocked_user_id;
    String user_id;

    public String getBlocked_user_id() {
        return blocked_user_id;
    }

    public void setBlocked_user_id(String blocked_user_id) {
        this.blocked_user_id = blocked_user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    
}
