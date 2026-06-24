import org.w3c.dom.ls.LSOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private DrawingCanvas dc;
    public Server(DrawingCanvas dc) throws IOException {
        serverSocket = new ServerSocket(4200);
        this.dc = dc;
    }

    public void listen() throws IOException {
        System.out.println("Server started");
        while(true){
            Socket socket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(dc);
            new Thread(()->{
                try{
                    handler.serveClient(socket);
                    System.out.println("Client connected");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    public static void main(String[] args) throws IOException {
        DrawingCanvas jpanel = new DrawingCanvas();
        Server server = new Server(jpanel);
        new Thread(() ->{
            try{
                server.listen();
            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
        SwingUtilities.invokeLater(() ->{
            jpanel.setBackground(Color.WHITE);
            JFrame frame = new JFrame();
            frame.setSize(500, 500);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(jpanel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int keyCode = e.getKeyCode();
                    if(keyCode == java.awt.event.KeyEvent.VK_LEFT) jpanel.moveOffSet(-10, 0);
                    if(keyCode == KeyEvent.VK_RIGHT) jpanel.moveOffSet(10, 0);
                    if(keyCode == KeyEvent.VK_UP) jpanel.moveOffSet(0, -10);
                    if(keyCode == KeyEvent.VK_DOWN) jpanel.moveOffSet(0, 10);
                }
            });
            frame.setFocusable(true);
            frame.requestFocusInWindow();
        });

    }
}
