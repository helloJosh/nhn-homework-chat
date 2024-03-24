package com.nhnacademy.client;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class ClientMain {
    
    public static void main(String[] args) {
        getClientOption(args);
    }
    public static void getClientOption(String[] args){
        String host = "localhost";
        int port = 1234;
        long client_id = 1L;
        Options options = new Options();
        options.addOption("H", false, "host");
        options.addOption("p", false, "port");
        options.addOption("i", true, "port");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if(cmd.hasOption("H")){
                host = cmd.getOptionValue("H");
            }
            if(cmd.hasOption("p")){
                port = Integer.parseInt(cmd.getOptionValue("p"));
            }
            if(cmd.hasOption("i")){
                client_id = Long.parseLong(cmd.getOptionValue("i"));
            }
            Client runClient = new Client(host, port, client_id);
            Thread thread = new Thread(runClient);
            thread.start();
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }

    }
}