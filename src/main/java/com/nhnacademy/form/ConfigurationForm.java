package com.nhnacademy.form;

import java.util.LinkedList;
import java.util.List;

public class ConfigurationForm {
    List<Long> clientIdList = new LinkedList<>();
    List<Long> denyClientIdList = new LinkedList<>();
    List<String> connect = new LinkedList<>();
    List<String> disconnect = new LinkedList<>();
    
    public ConfigurationForm() {
    }
    public ConfigurationForm(List<Long> clientIdList, List<Long> denyClientIdList, List<String> connect,
            List<String> disconnect) {
        this.clientIdList = clientIdList;
        this.denyClientIdList = denyClientIdList;
        this.connect = connect;
        this.disconnect = disconnect;
    }
    public List<Long> getClientIdList() {
        return clientIdList;
    }
    public void setClientIdList(List<Long> clientIdList) {
        this.clientIdList = clientIdList;
    }
    public List<Long> getDenyClientIdList() {
        return denyClientIdList;
    }
    public void setDenyClientIdList(List<Long> denyClientIdList) {
        this.denyClientIdList = denyClientIdList;
    }
    public List<String> getConnect() {
        return connect;
    }
    public void setConnect(List<String> connect) {
        this.connect = connect;
    }
    public List<String> getDisconnect() {
        return disconnect;
    }
    public void setDisconnect(List<String> disconnect) {
        this.disconnect = disconnect;
    }
    @Override
    public String toString() {
        return "ConfigurationForm [clientIdList=" + clientIdList + ", denyClientIdList=" + denyClientIdList
                + ", connect=" + connect + ", disconnect=" + disconnect + "]";
    }
    
}
