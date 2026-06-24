import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args){
        try{
            Socket socket = new Socket("localhost", 4200);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Client connected to server");
            Scanner scanner = new Scanner(System.in);
            while(true){
                String input = scanner.nextLine();
                writer.println(input);
                System.out.println("Send message: " + input);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
