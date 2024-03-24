package com.nhnacademy.form;

public class ConnectionRequestForm {
    private long id;
    private String type;
    private long client_id;
    public ConnectionRequestForm(){}

    public ConnectionRequestForm(long id, long client_id) {
        this.id = id;
        this.type = "connect";
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
    public long getClient_id() {
        return client_id;
    }
    public void setClient_id(long client_id) {
        this.client_id = client_id;
    }

    
}
