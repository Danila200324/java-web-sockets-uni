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
        Socket sock = new Socket();
        sock.connect(new InetSocketAddress(Inet4Address.getByName("172.21.48.20"), 15559));

        InputStream is = sock.getInputStream();

        OutputStream os = sock.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

        osw.append("101865\n");
        osw.flush();

        String s = readLine(is);
        System.out.println("recv 1: " + s);
        osw.append(s.replace("5", "")).append("\n");
        osw.flush();

        s = readLine(is);
        System.out.println("recv 2: " + s);
        long x = Long.parseLong(s);
        long val = 0;
        for(long i = 0; i * i * i * i * i <= x; i++) {
            val = i;
        }
        osw.append(Long.toString(val)).append("\n");
        osw.flush();

        osw.append(Integer.toString(sock.getLocalPort())).append("\n");
        osw.flush();

        s = readLine(is);
        System.out.println("recv 3: " + s);
    }
}
