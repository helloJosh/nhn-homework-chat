package com.nhnacademy.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.form.ClientListRequestForm;
import com.nhnacademy.form.ClientListResponseForm;
import com.nhnacademy.form.ConfigurationForm;
import com.nhnacademy.form.ConnectionRequestForm;
import com.nhnacademy.form.ConnectionValidForm;
import com.nhnacademy.form.MessageRequestForm;
import com.nhnacademy.form.MessageResponseForm;
import com.nhnacademy.repository.ClientRepository;
import com.nhnacademy.util.JsonFileUtility;
import com.nhnacademy.util.LogFileUtility;
import com.nhnacademy.util.Netcat;

public class Server implements Runnable{
    boolean monitor = false;
    long message_id=1;
    int port;
    Logger logger = LogManager.getLogger(getClass().getSimpleName());
    List<Thread> clientHandlerList = new LinkedList<>();
    List<Netcat> netcatList = new LinkedList<>();
    List<Long> clientIdList = new LinkedList<>();
    List<Long> denyClientIdList = new LinkedList<>();
    List<String> connect = new LinkedList<>();
    List<String> disconnect = new LinkedList<>();
    
    ClientRepository clientRepository = new ClientRepository();
    ObjectMapper objectMapper = new ObjectMapper();

    public Server(int port){
        this.port = port;
    }
    public int getPort(){
        return port;
    }
    public long getMessage_id() {
        return message_id++;
    }
    public void setMessage_id(long message_id) {
        this.message_id = message_id;
    }

    @Override
    public void run(){
        // 설정 파일 불러오기
        ConfigurationForm configurationInitForm = JsonFileUtility.jsonFileLoad();
        clientIdList = JsonFileUtility.deepCopy(configurationInitForm.getClientIdList());
        denyClientIdList = JsonFileUtility.deepCopy(configurationInitForm.getClientIdList());
        connect = JsonFileUtility.deepCopy(configurationInitForm.getConnect());
        disconnect = JsonFileUtility.deepCopy(configurationInitForm.getDisconnect());

        // 서버 관리 컨트롤러
        Thread userInputController = new Thread(()->{
            try(BufferedReader input = new BufferedReader(new InputStreamReader(System.in))){
                String line;
                while((line=input.readLine())!=null){
                    // client_list
                    if(line.equals("client_list")){        
                        System.out.println("=======Client List=======");
                        for(long l : clientIdList){
                            System.out.println("client Id :" +(int)l);
                        }
                        System.out.println();
                        logger.trace("Server client list call");
                    }
                    // deny add client_id 
                    if(line.matches("deny\\s+add\\s+\\d")){
                        String[] words = line.split(" ");
                        denyClientIdList.add(Long.parseLong(words[2]));
                        logger.trace("Server client deny added {}", words[2]);

                        ConfigurationForm configurationForm = new ConfigurationForm(clientIdList,denyClientIdList,connect,disconnect);
                        configurationForm.getDenyClientIdList().add(Long.parseLong(words[2]));
                        JsonFileUtility.jsonFileSave(configurationForm);
                    }
                    // deny del client_id
                    if(line.matches("deny\\s+del\\s+\\d")){
                        String[] words = line.split(" ");
                        denyClientIdList.remove(Long.parseLong(words[2]));

                        logger.trace("Server client deny removed {}", words[2]);
                        ConfigurationForm configurationForm = new ConfigurationForm(clientIdList,denyClientIdList,connect,disconnect);
                        configurationForm.getDenyClientIdList().remove(Long.parseLong(words[2]));
                        JsonFileUtility.jsonFileSave(configurationForm);
                    }
                    // monitor on
                    if(line.matches("monitor\\s+on")){
                        this.monitor = true;
                        logger.trace("Server message monitor on");
                    }
                    // monitor on
                    if(line.matches("monitor\\s+off")){
                        this.monitor = false;
                        logger.trace("Server message monitor off");
                    }
                    // send_off
                    if(line.equals("send_off\\s+\\d")){
                        String[] words = line.split(" ");
                        clientRepository.findById(Long.parseLong(words[2])).closeSocket();
                        logger.trace("Server client {} send off ", words[2]);
                    }
                    // log show s n
                    if(line.equals("log\\s+show\\s+[\\d]\\s+[\\d]")){
                        String[] words = line.split(" ");
                        List<String> logs = LogFileUtility.readLogFile();
                        if(words.length == 2){
                            for(int i=logs.size()-1;i>logs.size()-11;i--){
                                System.out.println(logs.get(i));
                            }
                        }
                        else if(words.length==3){
                            for(int i=logs.size()-1;i>logs.size()-Integer.parseInt(words[3])-1;i--){
                                System.out.println(logs.get(i));
                            }
                        }
                        else if(words.length ==4){
                            int s = Integer.parseInt(words[3]);
                            int n = Integer.parseInt(words[4]);
                            if(n>s){
                                for(int i=logs.size()-s;i>logs.size()-n-1;i--){
                                    System.out.println(logs.get(i));
                                }
                            } else {
                                System.out.println("message not valid default value printed");
                                for(int i=logs.size()-1;i>logs.size()-11;i--){
                                    System.out.println(logs.get(i));
                                }
                            }
                        }
                        logger.trace("Server log print ");
                    }
                    
                }
            }catch(IOException e){
                System.out.println(e.getMessage());
            }

        });

        // Client 입력 컨트롤러
        Thread clientInputController = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                synchronized(netcatList){
                    for(Netcat netcat : netcatList){
                        if(!netcat.isReceiveQueueEmpty()){
                            try{
                                String line = netcat.receive();
                                
                                // connection request 처리
                                if(line.matches("\\{\"id\":\\d+,\"type\":\"connect\",\"client_id\":\\d+\\}")){
                                    ConnectionRequestForm connectionRequestForm = new ConnectionRequestForm();
                                    connectionRequestForm = objectMapper.readValue(line, ConnectionRequestForm.class);
                                    synchronized(clientIdList){
                                        boolean flag = true;
                                        for(long l : clientIdList){
                                            if(l == connectionRequestForm.getClient_id()){
                                                netcat.send("Duplicated Client Id please connect again, Connection will be closed"+"\n");
                                                Thread.sleep(50);
                                                netcat.closeSocket();
                                                flag = false;
                                                ConfigurationForm configurationForm = new ConfigurationForm(clientIdList,denyClientIdList,connect,disconnect);
                                                configurationForm.getDisconnect().add("Time:"+ LocalDateTime.now() +",IP:"+netcat.getSocket().getInetAddress()+",Port:"+getPort());
                                                JsonFileUtility.jsonFileSave(configurationForm);
                                                logger.trace("Server client {} duplicated id Connection off ", connectionRequestForm.getClient_id());

                                            }
                                        }
                                        for(long l : denyClientIdList){
                                            if(l == connectionRequestForm.getClient_id()){
                                                netcat.send("You shall not pass!!!, Connection will be closed"+"\n");
                                                Thread.sleep(50);
                                                netcat.closeSocket();
                                                flag = false;
                                                ConfigurationForm configurationForm = new ConfigurationForm(clientIdList,denyClientIdList,connect,disconnect);
                                                configurationForm.getDisconnect().add("Time:"+ LocalDateTime.now() +",IP:"+netcat.getSocket().getInetAddress()+",Port:"+getPort());
                                                JsonFileUtility.jsonFileSave(configurationForm);
                                                logger.trace("Server client {} denied Connection off ", connectionRequestForm.getClient_id());
                                            }
                                        }
                                        if(flag){           
                                            clientIdList.add(connectionRequestForm.getClient_id());
                                            clientRepository.save(connectionRequestForm.getClient_id(), netcat);
                                            ConfigurationForm configurationForm = new ConfigurationForm(clientIdList,denyClientIdList,connect,disconnect);
                                            configurationForm.getConnect().add("Time:"+ LocalDateTime.now() +",IP:"+netcat.getSocket().getInetAddress()+",Port:"+getPort());
                                            JsonFileUtility.jsonFileSave(configurationForm);
                                        }
                                    }    
                                    ConnectionValidForm connectionValidForm = new ConnectionValidForm(getMessage_id(),
                                                                                                        connectionRequestForm.getClient_id());
                                    String connectionValid = objectMapper.writeValueAsString(connectionValidForm);
                                    netcat.send(connectionValid+"\n");
                                    logger.trace("Server client {} Connection valid ", connectionRequestForm.getClient_id());
                                }

                                // message request 처리
                                if(line.matches("\\{\"id\":\\d+,\"type\":\"\\w+\",\"target_id\":\\d+,\"message\":\"\\w+\"\\}")){
                                    MessageRequestForm messageRequest = new MessageRequestForm();
                                    messageRequest = objectMapper.readValue(line, MessageRequestForm.class);

                                    long target_id = messageRequest.getTarget_id();
                                    Netcat targetNetcat = clientRepository.findById(target_id);
                                    MessageResponseForm messageResponse = new MessageResponseForm(messageRequest.getId(), messageRequest.getMessage());
                                    targetNetcat.send(objectMapper.writeValueAsString(messageResponse)+"\n");
                                    if(monitor){
                                        System.out.println(messageRequest.getMessage());
                                    }
                                    logger.trace("Server sends message response to client {}", messageRequest.getTarget_id());
                                }

                                // client_list 처리
                                if(line.matches("\\{\"id\":\\d+,\"type\":\"client_list\"\\}")){
                                    ClientListRequestForm listRequestForm = objectMapper.readValue(line, ClientListRequestForm.class);
                                    
                                    ClientListResponseForm listResponseForm = new ClientListResponseForm(listRequestForm.getId(), clientIdList);
                                    String response = objectMapper.writeValueAsString(listResponseForm);
                                    System.out.println(response);
                                    netcat.send(response+"\n");
                                    logger.trace("Server sends client list response to client");
                                }
                            }catch(JsonProcessingException e){
                                System.err.println(e.getMessage());
                            } catch (InterruptedException e){
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignore) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        userInputController.start();
        clientInputController.start();

        try(ServerSocket serverSocket = new ServerSocket(getPort());) {
            while(!Thread.currentThread().isInterrupted()){
                Netcat netcat = new Netcat(serverSocket.accept());
                Thread thread = new Thread(netcat);
                System.out.println("Connected with client" + netcat.getSocket().getInetAddress() + netcat.getSocket().getRemoteSocketAddress());

     
                
                thread.start();
                clientHandlerList.add(thread);
                netcatList.add(netcat);        
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }
}