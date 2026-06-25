package server;

import org.example.Dot;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.Color;

public class Server {

    private ServerSocket serverSocket;
    private List<ClientThread> clients = new CopyOnWriteArrayList<>();
    private Connection dnConnection;
    private List<Dot> dots = new ArrayList<>();
    public Server() throws IOException {
        serverSocket = new ServerSocket(6666);
        try{
            initdb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
                    sendHistoryToClient(clientThread);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }



    public void broadcast(String message) throws SQLException {
        if(message.startsWith("NEW:")) {
            Dot dot = Dot.fromMessage(message); // fromMessage теперь должен уметь резать префикс
            saveDot(dot);
        }
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

    public void initdb() throws SQLException {
        String url = "jdbc:sqlite:canvas.db";
        String sql = "CREATE TABLE if not exists dot( " +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "x INTEGER NOT NULL, " +
                "y INTEGER NOT NULL, " +
                "color TEXT NOT NULL," +
                "radius INTEGER NOT NULL " +
                ");";

        dnConnection = DriverManager.getConnection(url);
        Statement stmt = dnConnection.createStatement();
        stmt.execute(sql);
    }

    public void saveDot(Dot dot) throws SQLException {
        String insert = "insert into dot(x, y, color, radius) values(?, ?, ?, ?);";
        PreparedStatement pstmt = dnConnection.prepareStatement(insert);
        pstmt.setDouble(1, dot.x());
        pstmt.setDouble(2, dot.y());
        String hexColor = String.format("#%02x%02x%02x",
                dot.color().getRed(), dot.color().getGreen(), dot.color().getBlue());
        pstmt.setString(3, hexColor);
        pstmt.setDouble(4, dot.r());
        pstmt.executeUpdate();
    }

    public List<Dot> getSavedDots() throws SQLException {
        List<Dot> savedDots = new ArrayList<>();
        String sql = "select x, y, color, radius from dot;";
        Statement stmt = dnConnection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            double x = rs.getDouble("x");
            double y = rs.getDouble("y");
            double r = rs.getDouble("radius");
            Color color = Color.decode(rs.getString("color"));
            Dot newDot = new Dot(x, y, r, color);
            savedDots.add(newDot);
        }
        return savedDots;

    }

    public void sendHistoryToClient(ClientThread client) throws SQLException {
        List<Dot> savedDots = getSavedDots();
        for(Dot dot : savedDots){
            client.send(Dot.toMessage(dot.x(), dot.y(), dot.r(), dot.color(), true));
        }
    }


}
