import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Main {
    private static String readLine(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = is.read()) != '\n') {
            if (c == -1) return sb.toString();
            if (c == '\r') continue;

            sb.append((char) c);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        ServerHandler sh = new ServerHandler();
        sh.initializeServer();


        Socket sock = new Socket();
        sock.connect(new InetSocketAddress(Inet4Address.getByName("172.21.48.31"), 25559));

        InputStream is = sock.getInputStream();

        OutputStream os = sock.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

        osw.append("149756\n");
        osw.flush();
        osw
                .append(sock.getLocalAddress().getHostAddress())
                .append(":")
                .append(String.valueOf(sh.sockServ.getLocalPort()))
                .append("\n");
        osw.flush();

        sock.close();
    }
}
