package com.nhnacademy.form;

public class ConnectionValidForm {
    private long id;
    String type;
    String response;
    long client_id;
    public ConnectionValidForm(){}
    public ConnectionValidForm(long id,long client_id) {
        this.id = id;
        this.type = "connect";
        this.response = "ok";
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
    public long getClient_id() {
        return client_id;
    }
    public void setClient_id(long client_id) {
        this.client_id = client_id;
    }

    
}
