import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BasicClient {
    JTextArea receivedMess;
    JTextField message;
    BufferedReader reader;
    PrintWriter writer;
    Socket socket;

    public static void main(String[] args){
            BasicClient client = new BasicClient();
            client.setup();
    }

    public void setup() {
        //CREATING FRAME SETUP
        JFrame frame = new JFrame("Basic Client Communicator");
        JPanel mainPanel = new JPanel();

        receivedMess = new JTextArea(15,50);
        receivedMess.setLineWrap(true);
        receivedMess.setWrapStyleWord(true);
        receivedMess.setEditable(false);

        JScrollPane scroll = new JScrollPane(receivedMess);

        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        message = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new sendButtonListener());

        mainPanel.add(scroll);
        mainPanel.add(message);
        mainPanel.add(sendButton);
        configureCommunication();

        Thread receiverThread = new Thread(new commReceiver()); //CREATING NEW THREAD WHICH WILL RECEIVE AND SHOW MESSAGES FROM OTHERS
        receiverThread.start();

        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(600,500);
        frame.setVisible(true);
    }

    private void configureCommunication() {
        //CONFIGURE CONNECTION WITH SERVER
        try{
            socket = new Socket("127.0.0.1", 5000); //SERVER WILL WORK ON LOCAL HOST, PORT 5000
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(socket.getOutputStream());
            System.out.println("network support prepared");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public class sendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev){
            try {
                writer.println(message.getText());
                writer.flush();

            } catch(Exception ex) {
                ex.printStackTrace();
            }
            message.setText("");
            message.requestFocus();
        }

    }

    public class commReceiver implements Runnable{
        public void run(){
            String mess;
            try {
                while((mess = reader.readLine()) != null){
                    System.out.println("Received: "+ mess);
                    receivedMess.append(mess + "\n");
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}