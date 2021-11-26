import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String username;

    public String toString(){
        return username;
    }

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = br.readLine();
            clientHandlers.add(this);
            broadcastMessage(username + " has joined the chat.");
            showCurrentUsers();
            

        } catch (IOException e){
            closeConnection(socket, br, bw);
        }
    }

    @Override
    public void run() {
        String clientMessage;

        while (socket.isConnected()) {
            try {
                clientMessage = br.readLine();
                broadcastMessage(clientMessage);
            } catch (IOException e) {
                closeConnection(socket, br, bw);
                break;
            }
        }
    }
    
    public void showCurrentUsers() {
        for (ClientHandler client : clientHandlers) {
            try {
                if (client.username.equals(username)) {
                    System.out.println(clientHandlers.size());
                    if (clientHandlers.size() == 1) {
                       client.bw.write("You're the first one here!");
                        client.bw.newLine();
                        client.bw.flush(); 
                    } else {
                        client.bw.write("Current users in chat: " + clientHandlers.toString());
                        client.bw.newLine();
                        client.bw.flush();
                    }
                } 
            } catch (IOException e){
                closeConnection(socket, br, bw);
            }
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler client : clientHandlers) {
            try {
                if (!client.username.equals(username)){
                    client.bw.write(message);
                    client.bw.newLine();
                    client.bw.flush();
                } 
            } catch (IOException e){
                closeConnection(socket, br, bw);
            }
        }
    }

    public void removeClient() {
        clientHandlers.remove(this);
        broadcastMessage(username + " has left.");
    }

    public void closeConnection(Socket socket, BufferedReader br, BufferedWriter bw) {
        removeClient();
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
}