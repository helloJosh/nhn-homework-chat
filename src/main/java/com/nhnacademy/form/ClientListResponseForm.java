package com.nhnacademy.form;

import java.util.List;

public class ClientListResponseForm {
    private long id;
    private String cmd;
    private List<Long> clientList;

    
    public ClientListResponseForm() {
    }
    
    public ClientListResponseForm(long id, List<Long> clientList) {
        this.id = id;
        this.cmd = "client_list";
        this.clientList = clientList;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getCmd() {
        return cmd;
    }
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
    public List<Long> getClientList() {
        return clientList;
    }
    public void setClientList(List<Long> clientList) {
        this.clientList = clientList;
    }
}
