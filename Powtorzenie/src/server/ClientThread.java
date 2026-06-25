package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ClientThread implements Runnable{

    private Socket socket;
    private Server server;
    private BufferedReader reader;
    private PrintWriter writer;


    public ClientThread(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }


    @Override
    public void run() {
        try{
            String message;
            while((message = reader.readLine()) != null){
                server.broadcast(message);
            }
        } catch (IOException e) {
            System.err.println("Connection was ended: " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String message){
        writer.println(message);
    }
}
