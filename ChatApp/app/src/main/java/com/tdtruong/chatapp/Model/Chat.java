package com.tdtruong.chatapp.Model;

public class Chat {
    private String uid_sender;
    private String ipaddr_sender;
    private String ipaddr_receiver;
    private String uid_receiver;
    private String message;
    private String type;
    private boolean isseen;

    public Chat(String uid_sender, String ipaddr_sender, String ipaddr_receiver, String uid_receiver, String message, String type, boolean isseen) {
        this.uid_sender = uid_sender;
        this.ipaddr_sender = ipaddr_sender;
        this.ipaddr_receiver = ipaddr_receiver;
        this.uid_receiver = uid_receiver;
        this.message = message;
        this.type = type;
        this.isseen = isseen;
    }

    public Chat() {
    }

    public String getUid_sender() {
        return uid_sender;
    }

    public void setUid_sender(String uid_sender) {
        this.uid_sender = uid_sender;
    }

    public String getIpaddr_sender() {
        return ipaddr_sender;
    }

    public void setIpaddr_sender(String ipaddr_sender) {
        this.ipaddr_sender = ipaddr_sender;
    }

    public String getIpaddr_receiver() {
        return ipaddr_receiver;
    }

    public void setIpaddr_receiver(String ipaddr_receiver) {
        this.ipaddr_receiver = ipaddr_receiver;
    }

    public String getUid_receiver() {
        return uid_receiver;
    }

    public void setUid_receiver(String uid_receiver) {
        this.uid_receiver = uid_receiver;
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

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}