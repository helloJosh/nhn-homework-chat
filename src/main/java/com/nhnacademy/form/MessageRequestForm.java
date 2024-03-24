package com.nhnacademy.form;

public class MessageRequestForm {
    private long id;
    private String type;
    private long target_id;
    private String message;
    public MessageRequestForm(){
    }

    public MessageRequestForm(long id,long target_id, String message) {
        this.id = id;
        this.type = "message";
        this.target_id = target_id;
        this.message = message;
    }
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public long getTarget_id() {
        return target_id;
    }
    public void setTarget_id(long target_id) {
        this.target_id = target_id;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
