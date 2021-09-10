package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    protected static ExecutorService pool = Executors.newFixedThreadPool(8);
    protected static int index = 0;
    protected static ArrayList<String> paths = new ArrayList<>();
    protected static ArrayList<Boolean> adminAccess = new ArrayList<>();

    public static void main(String[] args) {
        ServerSocket serverSocket = runServer();

        while (true) {
            paths.add("D:\\");
            adminAccess.add(false);
            connectClient(serverSocket);
            index++;
        }

    }

    private static void connectClient(ServerSocket serverSocket) {
        Socket socket = null;
        try {
            if (serverSocket != null) {
                socket = serverSocket.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("New client is connected");

        ClientHandler clientHandler = new ClientHandler(socket, serverSocket, index);
        pool.execute(clientHandler);
        System.out.println("Number of connected clients is " + (((ThreadPoolExecutor) pool).getActiveCount()));
    }

    private static ServerSocket runServer() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(7777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (serverSocket != null) {
                serverSocket.setReuseAddress(true);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        System.out.println("Server is running");
        return serverSocket;
    }
}
