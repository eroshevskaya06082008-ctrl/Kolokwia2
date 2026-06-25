package client;

import org.example.Dot;
import server.Server;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ServerThread implements Runnable{

    private PrintWriter writer;
    private BufferedReader reader;
    private Consumer<Dot> consumer;

    public ServerThread(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void setOnDotReceiver(Consumer<Dot> consumer){
        this.consumer = consumer;

    }

    @Override
    public void run() {
        try{
            String line;
            while((line = reader.readLine()) != null){
                Dot dot = Dot.fromMessage(line);
                if(consumer != null){
                    consumer.accept(dot);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void send(double x, double y, double r, Color color){
        if(writer != null){
            String mes = Dot.toMessage(x, y, r, color);
            writer.println(mes);
        }
    }

    public void send(String mes){
        writer.println(mes);
    }


}
