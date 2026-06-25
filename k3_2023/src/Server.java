import javax.imageio.ImageIO;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Server {
    private boolean isClientActive = false;
    private ServerSocket serverSocket;
    private Interface in;
    int millis = 0;
    private int radius;

    private Connection dbConnection;
    public Server() throws IOException {
        this.serverSocket = new ServerSocket(5000);
        in = new Interface();

    }

    public void listen() {
        System.out.println("Server was created");
        try{
            Socket socket = serverSocket.accept();
            System.out.println("new client connected");
            isClientActive = true;
            new Thread(() ->{
                serveClient(socket);
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void serveClient(Socket socket) {
        try{
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-mm-yyyy");
            String path = dateTime.format(df) + ".png";
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
            File folder = new File("images");
            if(folder.mkdir()){
                System.out.println("folder created");
            }
            FileOutputStream output = new FileOutputStream("images/"+ path);
            BufferedImage image = ImageIO.read(new File("images/"+ path));
            saveImage(input, output);
            this.radius = in.getSlider().getValue();
            BufferedImage newImg = blur(radius, path);
            ImageIO.write(newImg, "png", new File("images/newImg.png"));
            initDB();
            insert(path, radius, millis);
            File file = new File("images/newImg.png");
            FileInputStream input1 = new FileInputStream(file);
            DataOutputStream output1 = new DataOutputStream(socket.getOutputStream());
            sendToClient(output1, input1, file);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void saveImage( DataInputStream input, FileOutputStream output) throws IOException {
        byte[] buffer = new byte[8192];
        int count;
        int receivedSize = 0;
        long fileSize = input.readLong();

        while (receivedSize < fileSize) {
            count = input.read(buffer);
            output.write(buffer, 0, count);
            System.out.println(count);
            receivedSize += count;
        }
        output.close();

        System.out.println("File created.");
    }


    public BufferedImage blur( int radius, String path) throws IOException {
        long startTime = System.currentTimeMillis();

        BufferedImage src = ImageIO.read(new File("images/"+ path));

        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage dest = new BufferedImage(w, h, src.getType());

        for (int y = radius; y < h - radius; y++) {
            for (int x = radius; x < w - radius; x++) {
                int r = 0, g = 0, b = 0;
                int count = 0;

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        int rgb = src.getRGB(x + dx, y + dy);
                        r += (rgb >> 16) & 0xFF;
                        g += (rgb >> 8) & 0xFF;
                        b += (rgb) & 0xFF;
                        count++;
                    }
                }


                r /= count; g /= count; b /= count;
                int newRgb = (r << 16) | (g << 8) | b;
                dest.setRGB(x, y, newRgb);
            }
        }
        long endTime = System.currentTimeMillis();
        long delay = endTime - startTime;
        this.millis = (int)delay;
        return dest;
    }

    public void initDB() throws SQLException {
        String url = "jdbc:sqlite:images/files.db";
        this.dbConnection = DriverManager.getConnection(url);
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS files(
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    path text NOT NULL,
                    size INTEGER NOT NULL,
                    delay INTEGER NOT NULL
                );
                """;
        try(Statement stmt = dbConnection.createStatement()){
            stmt.execute(createTableSQL);
        }
    }

    public void insert(String path, int radius, int delay){
        String sql = "insert into files(path, size, delay) values (?, ?, ?)";
        try(PreparedStatement ptstmt = dbConnection.prepareStatement(sql)){
            ptstmt.setString(1, path);
            ptstmt.setInt(2, radius);
            ptstmt.setInt(3, delay);
            ptstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendToClient(DataOutputStream output, FileInputStream input, File file) throws IOException {
        byte[] buffer = new byte[8192];
        int count;
        output.writeLong(file.length());
        while ((count = input.read(buffer)) != -1)
            output.write(buffer, 0, count);

        output.flush();

        System.out.println("File sent.");
    }




}
