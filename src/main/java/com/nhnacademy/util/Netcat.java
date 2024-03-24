package com.nhnacademy.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Netcat implements Runnable{
    Socket socket;
    Queue<String> receiveQueue = new LinkedList<>();
    Queue<String> sendQueue = new LinkedList<>();

    public Socket getSocket(){
        return socket;

    }
    public Netcat(Socket socket) {
        this.socket = socket;
    }
    public void closeSocket(){
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    public void send(String object){
        synchronized(sendQueue){
            sendQueue.add(object);
        }
    }
    public boolean isReceiveQueueEmpty(){
        synchronized(receiveQueue){
            return receiveQueue.isEmpty();
        }
    }
    public String receive(){
        synchronized(receiveQueue){
            return receiveQueue.poll();
        }
    }
    public void run(){
        try(BufferedReader inputRemote = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter outputRemote = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            Thread receiver = new Thread(()->{
                try{
                    String line;
                    while((line=inputRemote.readLine())!=null){
                        synchronized(receiveQueue){
                            receiveQueue.add(line);
                        }
                    }
                } catch(IOException e){
                    System.err.println(e.getMessage());
                }
            });
            Thread sender = new Thread(()->{
                try{
                    while(!Thread.currentThread().isInterrupted()){
                        synchronized(sendQueue){
                            if(!sendQueue.isEmpty()){
                                outputRemote.write(sendQueue.poll());
                                outputRemote.flush();
                            }
                        }
                    }
                } catch(IOException e){
                    System.err.println(e.getMessage());
                }
            });

            receiver.start();
            sender.start();

            receiver.join();
            sender.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
