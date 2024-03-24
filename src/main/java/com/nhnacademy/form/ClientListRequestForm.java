package com.nhnacademy.form;

public class ClientListRequestForm {
    private long id;
    private String type;
    
    public ClientListRequestForm(){}
    public ClientListRequestForm(long id) {
        this.id = id;
        this.type = "client_list";
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
}
