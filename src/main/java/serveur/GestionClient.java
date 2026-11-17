package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GestionClient implements Runnable {
    Socket socket;

    public GestionClient(Socket s) {
        this.socket = s;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

                String ligne;
                while ((ligne = br.readLine()) != null) {
                    pw.println(ligne);
                    // TODO: enlever après tests
                    System.out.println(ligne);
                }
        } catch (Exception e) {
            System.out.println("Erreur.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
