package com.nhnacademy.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ServerMain {
    public static void main(String[] args){
        getServerOption(args);
    }
    public static void getServerOption(String[] args){
        Options options = new Options();
        int port= 1234;
        options.addOption("p", false, "Port");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if(cmd.hasOption("p")){
                port = Integer.parseInt(cmd.getOptionValue("p"));
            }
            Server server = new Server(port);
            Thread serverThread = new Thread(server);
            serverThread.start();
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }
}
