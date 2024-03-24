package com.nhnacademy.form;

public class MessageResponseForm {
    private long id;
    private String type;
    private long client_id;
    private String message;
    public MessageResponseForm(){
    }

    public MessageResponseForm(long id, String message) {
        this.id = id;
        this.type = "message";
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
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
