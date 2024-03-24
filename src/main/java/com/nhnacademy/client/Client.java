package com.nhnacademy.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalDateTime;

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
import com.nhnacademy.util.Netcat;

public class Client implements Runnable{
    //private Socket socket;
    private String host;
    private int port;
    private long client_id;
    private long message_id=client_id;
    ClientRepository clientRepository = new ClientRepository();
    StringBuilder resultBuilder = new StringBuilder();
    ObjectMapper objectMapper = new ObjectMapper();
    Logger logger = LogManager.getLogger(getClass().getSimpleName());

    public Client(String host, int port, long client_id){
        this.host = host;
        this.port = port;
        this.client_id = client_id;
    }
    public int getPort(){
        return this.port;
    }
    public String getHost(){
        return this.host;
    }
    
    public long getClient_id() {
        return client_id;
    }
    public void setClient_id(long client_id) {
        this.client_id = client_id;
    }
    public long getMessage_id() {
        return message_id++;
    }
    public void setMessage_id(long message_id) {
        this.message_id = message_id;
    }
    @Override
    public void run(){
        Socket socket;
        try {
            socket = new Socket(getHost(), getPort());
            System.out.println("Connected with server,  Client info :"+socket.getRemoteSocketAddress());

            Netcat netcat = new Netcat(socket);
            Thread thread = new Thread(netcat);
            
            thread.start();
            ConnectionRequestForm connectionRequestForm = new ConnectionRequestForm(getMessage_id(),getClient_id());
            String connectRequestLine = objectMapper.writeValueAsString(connectionRequestForm);
            netcat.send(connectRequestLine+"\n");

            Thread userInputHandler = new Thread(()->{
                try{
                    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                    String line;
                    while((line=input.readLine())!=null){
                        if(line.matches("/r\\s+\\d+\\s+.+")){
                            String[] words = line.split(" ");
                            String target_id = words[1];
                            for (int i = 2; i < words.length; i++) {
                                resultBuilder.append(words[i]);
                                if (i < words.length - 1) {
                                    resultBuilder.append(" "); // 마지막 단어를 제외한 나머지 단어 사이에 공백을 추가합니다.
                                }
                            }
                            String message = resultBuilder.toString();
                            
                            MessageRequestForm messageRequest = new MessageRequestForm(getMessage_id(),
                                                                    Long.parseLong(target_id),message);
                            String requestMessage = objectMapper.writeValueAsString(messageRequest);
                            System.out.println(requestMessage);
                            netcat.send(requestMessage+"\n");
                            logger.trace("client message request");
                                                                    
                        }
                        if(line.equals("client_list")){
                            ClientListRequestForm clientListRequest = new ClientListRequestForm(getMessage_id());
                            String listRequest = objectMapper.writeValueAsString(clientListRequest);
                            netcat.send(listRequest+"\n");
                            System.out.println(listRequest);
                            logger.trace("Client request list call ");
                        }
                        else{
                            netcat.send(line+"\n");
                        }
                    }
                } catch (IOException e){
                    System.err.println(e.getMessage());
                }
            });

            Thread serverInputHandler = new Thread(()->{
                while(!Thread.currentThread().isInterrupted()){
                    if(!netcat.isReceiveQueueEmpty()){
                        try{
                            String line = netcat.receive();
                            if(line.matches("\\{\"id\":\\d+,\"type\":\"\\w+\",\"response\":\"\\w+\",\"client_id\":\\d+\\}")){
                                ConnectionValidForm connectionValidForm = new ConnectionValidForm();
                                connectionValidForm = objectMapper.readValue(line, ConnectionValidForm.class);
                                System.out.println("Connection Valid , Your id is "+connectionValidForm.getClient_id());
                                setMessage_id(connectionValidForm.getId());

                                logger.trace("client receive connection valid response");
                            }
                            if(line.matches("\\{\"id\":\\d+,\"type\":\"\\w+\",\"message\":\"\\w+\"\\}")){
                                MessageResponseForm messageResponseForm = new MessageResponseForm();
                                messageResponseForm = objectMapper.readValue(line, MessageResponseForm.class);
                                System.out.println(messageResponseForm.getMessage());
                                setMessage_id(messageResponseForm.getId());

                                logger.trace("client receive message response");
                            }
                            System.out.println(line);
                            if(line.matches("\\{\"id\":\\d+,\"cmd\":\"client_list\",\"clientList\":\\[\\d+(\\s*,\\d+)*\\]\\}")){
                                ClientListResponseForm listResponseForm = new ClientListResponseForm();
                                listResponseForm = objectMapper.readValue(line, ClientListResponseForm.class);
                                setMessage_id(listResponseForm.getId());
                                System.out.println("=======client list========");
                                for(long l :listResponseForm.getClientList()){
                                    System.out.println("Client id :"+l);
                                }
                                System.out.println();
                                logger.trace("client receive client list response");
                            }
                        } catch (JsonProcessingException e){
                            System.err.println(e.getMessage());
                        }
                    }
                }
            });
            userInputHandler.start();
            serverInputHandler.start();
            thread.join();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
