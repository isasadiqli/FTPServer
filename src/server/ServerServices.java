package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ServerServices {
    protected final static String administratorPassword = "admin123";

    protected static String requestLine = "> ";

    protected static String readFromClient(BufferedReader bufferedReader) throws IOException {
        String s = "";
        while (bufferedReader.read() != '~')
            s = bufferedReader.readLine();
        return s;
    }

    protected static void writeStringToClient(String s, PrintWriter printWriter) {
        printWriter.println(s);
        printWriter.println("~");
        printWriter.flush();
    }
}
