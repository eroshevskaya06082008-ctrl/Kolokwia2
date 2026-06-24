import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler {
    private DrawingCanvas dc;
    private Graphics g;

    public ClientHandler(DrawingCanvas dc){
        this.dc = dc;

    }
    public void serveClient(Socket socket){
        BufferedReader reader;
        Color color = Color.BLACK;
        try{
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            while((message = reader.readLine()) != null){
                System.out.println("Received message: " + message);
                if(!message.contains(" ")){
                    color = Color.decode(message.trim());
                } else if(message.contains(" ")){
                    String[] parts = message.split(" ");
                    double x1 = Double.parseDouble(parts[0]);
                    double y1 = Double.parseDouble(parts[1]);
                    double x2 = Double.parseDouble(parts[2]);
                    double y2 = Double.parseDouble(parts[3]);
                    System.out.println("DEBUG: Рисуем линию из (" + x1 + "," + y1 + ") в (" + x2 + "," + y2 + ") цветом " + color);
                    LineData line = new LineData(x1, y1, x2, y2, color);
                    dc.addLine(line);

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
