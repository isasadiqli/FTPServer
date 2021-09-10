package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ServerSocket serverSocket;
    private PrintWriter printWriter = null;
    private BufferedReader bufferedReader = null;
    private int index;

    public ClientHandler(Socket socket, ServerSocket serverSocket, int index) {
        this.socket = socket;
        this.serverSocket = serverSocket;
        this.index = index;
    }

    @Override
    public void run() {

        try {

            if (socket.isClosed()) {
                socket = serverSocket.accept();
                System.out.println("server was closed");
            }

            printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            requestCommand();

        } catch (IOException e) {
            System.out.println("Connection with client lost");
        }

    }

    private void requestCommand() throws IOException {
        Commands command = new Commands(ServerServices.readFromClient(bufferedReader), bufferedReader, printWriter, socket, index);
        System.out.println("Command received as: " + command.getCommand());
        command.exec();
        if (!command.getCommand().equals("bye") && !command.getCommand().equals("exit"))
            requestCommand();
    }
}
