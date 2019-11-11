package com.tdtruong.chatapp.Model;

public class GroupChat {
    private String imageURL;
    private String ipaddr_sender;
    private String message;
    private String type;
    private String uid_sender;
    private String user_name;

    public GroupChat(String imageURL, String ipaddr_sender, String message, String type, String uid_sender, String user_name) {
        this.imageURL = imageURL;
        this.ipaddr_sender = ipaddr_sender;
        this.message = message;
        this.type = type;
        this.uid_sender = uid_sender;
        this.user_name = user_name;
    }

    public GroupChat(){

    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getIpaddr_sender() {
        return ipaddr_sender;
    }

    public void setIpaddr_sender(String ipaddr_sender) {
        this.ipaddr_sender = ipaddr_sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid_sender() {
        return uid_sender;
    }

    public void setUid_sender(String uid_sender) {
        this.uid_sender = uid_sender;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
