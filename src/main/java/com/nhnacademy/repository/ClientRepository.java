package com.nhnacademy.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nhnacademy.util.Netcat;

public class ClientRepository {
    Map<Long, Netcat> clientMap = new ConcurrentHashMap<>();
    
    public long save(long client_id, Netcat netcat){
        synchronized(clientMap){
            clientMap.put(client_id,netcat);
            return client_id;
        }
    }
    public synchronized Netcat findById(long client_id){
        return clientMap.get(client_id);
}
    public synchronized List<Netcat> findAll(){
        List<Netcat> netcatList = new LinkedList<>();
        for(long i=0;i<clientMap.size()+1;i++){
            netcatList.add(clientMap.get(i));
        }
        return netcatList;
    }
}
