package server;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private ServerSocket serverSocket;
    private List<ClientThread> clients = new CopyOnWriteArrayList<>();

    public Server() throws IOException {
        serverSocket = new ServerSocket(6666);
    }
    public void listen() throws IOException {
        System.out.println("Server started");
        while(true){
            new Thread(() -> {
                try{
                    Socket socket = serverSocket.accept();
                    ClientThread clientThread = new ClientThread(socket, this);

                    System.out.println("New client connected");
                    clients.add(clientThread);
                    new Thread(clientThread).start();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }



    public void broadcast(String message){
        for(ClientThread client : clients){
            client.send(message);
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            try {
                new Server().listen();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
