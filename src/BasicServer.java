import java.io.*;
import java.net.*;
import java.util.*;

public class BasicServer {
    ArrayList outputStreams;

    public class clientService implements Runnable {
        BufferedReader reader;
        Socket socket;
        public clientService(Socket clientSocket){
            try {
                socket = clientSocket;
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void run() {
            String message;
            try {
                while((message = reader.readLine()) != null) {
                    System.out.println("Received: " + message);
                    sendToAll(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new BasicServer().setup();
    }

    public void setup(){
        outputStreams = new ArrayList();
        
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while(true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                outputStreams.add(writer);

                Thread t = new Thread(new clientService(clientSocket));
                t.start();
                System.out.println("connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendToAll(String message){
        Iterator it = outputStreams.iterator();
        while(it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
