package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5001);

        while (true) {
            Socket s = serverSocket.accept();

            new Thread(new GestionClient(s)).start();
        }
    }
}
