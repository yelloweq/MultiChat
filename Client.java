import java.net.*;
import java.util.Scanner;

import java.io.*;

public class Client {

    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String username;

    public Client(Socket socket, String username) {

        try {
            this.socket = socket;
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeConnection(socket, br, bw);
        }

    }
    
    public void sendMessage() {
        try {
            bw.write(username);
            bw.newLine();
            bw.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                bw.write(username + " >> " + message);
                bw.newLine();
                bw.flush();

            }
            scanner.close();
        } catch (IOException e) {
            closeConnection(socket, br, bw);
        }
    }
    
    public void receiveMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;

                while (socket.isConnected()) {
                    try {
                        message = br.readLine();
                        System.out.println(message);
                    } catch (IOException e) {
                        closeConnection(socket, br, bw);
                    }

                }
            }
        }).start();
    }
    
    public void closeConnection(Socket socket, BufferedReader br, BufferedWriter bw) {
        try {
            if (br != null){
                br.close();
            }
            if (bw != null){
                bw.close();
            }
            if (socket != null){
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 5001);
        Client client = new Client(socket, username);
        client.receiveMessage();
        client.sendMessage();
        if (!socket.isConnected()) {
            scanner.close();
        }
    }
}
