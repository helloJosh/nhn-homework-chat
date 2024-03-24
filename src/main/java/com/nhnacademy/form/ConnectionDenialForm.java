package com.nhnacademy.form;

public class ConnectionDenialForm {
    private long id;
    private String type;
    private String response;
    private String client_id;
    
    public ConnectionDenialForm() {
    }
    
    public ConnectionDenialForm(long id, String client_id) {
        this.id = id;
        this.type = "connect";
        this.response = "deny";
        this.client_id = client_id;
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
    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }
    public String getClient_id() {
        return client_id;
    }
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
    
}
