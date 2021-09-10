package server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Commands {
    private String command;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private Socket socket;
    private int index;

    public Commands(String command, BufferedReader bufferedReader, PrintWriter printWriter, Socket socket, int index) {
        this.command = command;
        this.bufferedReader = bufferedReader;
        this.printWriter = printWriter;
        this.socket = socket;
        this.index = index;
    }

    public String getCommand() {
        return command;
    }

    protected void bye() {
        try {
            socket.close();
            printWriter.close();
            bufferedReader.close();
            Main.adminAccess.set(index, false);
            System.out.println("1 client is disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void exit() {
        bye();
    }

    protected void close() {

    }

    protected void delete(String fileName) {
        String message;
        File file = new File(Main.paths.get(index) + fileName);

        if (file.delete())
            message = file.getName() + " is deleted";
        else
            message = "Delete operation is failed, because " + fileName + " does not exists";

        System.out.println(message);
        printWriter.println(message);
    }

    protected void download(String fileName) {

        try {

            File file = new File(Main.paths.get(index) + fileName);
            System.out.println("File name " + file.getName());

            byte[] b_arr = Files.readAllBytes(Paths.get(String.valueOf(file)));
            System.out.println("length of b array" + b_arr.length);
            printWriter.println(b_arr.length);

            OutputStream outputStream = socket.getOutputStream();

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(b_arr, 0, b_arr.length);
            bufferedOutputStream.flush();

            System.out.println("File send is successful");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void upload(String fileName) {
        try {
            bufferedReader.readLine();
            int length = Integer.parseInt(bufferedReader.readLine());
            System.out.println("length of b array" + length);

            InputStream inputStream = null;

            inputStream = socket.getInputStream();

            System.out.println("filename " + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(Main.paths.get(index) + fileName);

            byte[] b_arr = new byte[length];

            inputStream.read(b_arr, 0, length);

            fileOutputStream.write(b_arr, 0, b_arr.length);

            fileOutputStream.close();

            System.out.println("File receive is successful");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    protected void ls() {
        System.out.println("ls path " + Main.paths.get(index));

        File file = new File(Main.paths.get(index));
        File[] files = file.listFiles();

        if (files != null) {
            Arrays.sort(files);

            StringBuilder s = new StringBuilder();
            int i = 0;

            for (File f : files) {
                i++;
                s.append(i).append(". ").append(f.getName()).append(";");
            }

            printWriter.println(s);
        }

    }

    protected void cd() {
        System.out.print(ServerServices.requestLine + "directory> ");

        try {
            String d = ServerServices.readFromClient(bufferedReader);
            String p;

            File file = new File(Main.paths.get(index));
            File[] files = file.listFiles();
            ArrayList<String> filesAL = new ArrayList<>();

            if (files != null) {
                for (File f :
                        files) {
                    filesAL.add(f.getName());
                }

                if (filesAL.contains(d)) {

                    Main.paths.set(index, Main.paths.get(index) + (d + "\\"));
                    ServerServices.requestLine = "> " + Main.paths.get(index) + "> ";

                    p = Main.paths.get(index);

                    System.out.println(ServerServices.requestLine);
                    System.out.println(Main.paths.get(index));
                    System.out.println(index);
                } else
                    p = "dnte";
            } else
                p = "ie";

            printWriter.println(p);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void help() {
        System.out.println("Client got help");
    }

    protected void login(String password) {

        System.out.println(password);
        if (!password.equals(ServerServices.administratorPassword))
            printWriter.println("Admin access denied");
        else {
            Main.adminAccess.set(index, true);
            printWriter.println("");
        }

    }

    protected void exec() {
        String[] c = command.split(" ", 2);
        System.out.println("c size" + c.length);
        switch (c[0]) {
            case "bye":
                bye();
                break;

            case "exit":
                exit();
                break;

            case "close":
                if (Main.adminAccess.get(index))
                    close();
                else
                    System.out.println("Client does not have admin access");
                break;

            case "delete":
                if (Main.adminAccess.get(index))
                    if (c.length == 2)
                        delete(c[1]);
                    else
                        System.out.println("Wrong format");
                else
                    System.out.println("Client does not have admin access");
                break;

            case "download":
                download(c[1]);
                break;

            case "upload":
                if (Main.adminAccess.get(index))
                    if (c.length == 2)
                        upload(c[1]);
                    else
                        System.out.println("Wrong format");
                else
                    System.out.println("Client does not have admin access");
                break;

            case "ls":
                ls();
                break;

            case "cd":
                if (c.length == 2)
                    cd();
                else
                    Main.paths.set(index, "D:\\");
                break;

            case "help":
                help();
                break;

            case "login":
                if (c.length == 2)
                    login(c[1]);
                else
                    System.out.println("Wrong format");
                break;

            default:
                System.out.println("Unknown command. To see commands enter help");
        }
    }
}
