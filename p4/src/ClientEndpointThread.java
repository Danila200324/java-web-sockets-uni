import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClientEndpointThread extends Thread {
    private final ServerHandler serverHandler;
    private final Socket endpointSock;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final OutputStreamWriter osw;

    public ClientEndpointThread(ServerHandler serverHandler, Socket endpointSock) throws IOException {
        this.serverHandler = serverHandler;
        this.endpointSock = endpointSock;
        this.inputStream = endpointSock.getInputStream();
        this.outputStream = endpointSock.getOutputStream();
        this.osw = new OutputStreamWriter(outputStream, StandardCharsets.US_ASCII);
        setName("Client endpoint thread");
    }

    private String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = this.inputStream.read()) != '\n') {
            if (c == -1) return sb.toString();
            if (c == '\r') continue;

            sb.append((char) c);
        }
        return sb.toString();
    }

    private void writeLine(String s) throws IOException {
        osw.append(s);
        osw.append('\n');
        osw.flush();
    }

    @Override
    public void run() {
        try {
            String s = readLine();
            System.out.println("recv0 from " + endpointSock.getRemoteSocketAddress() + ": " + s);
            long lValueSum;
            {
                long l = Long.parseLong(s);
                synchronized (serverHandler.fpLock) {
                    long a = l, b = serverHandler.gValueGcd;
                    while(b != 0) {
                        a %= b;
                        long tmp = a;
                        a = b;
                        b = tmp;
                    }
                    serverHandler.gValueGcd = a;
                    lValueSum = (serverHandler.gValueSum += l);
                }
            }
            writeLine(s);
            writeLine(Integer.toString((int) lValueSum));
            long sum = 0;
            for(int i = 0; i < 5; i++) {
                String s2 = readLine();
                System.out.println("recv2 from " + endpointSock.getRemoteSocketAddress() + ": " + s2);
                sum += Long.parseLong(s2);
            }
            writeLine(Long.toString(sum));
            writeLine(Integer.toString(serverHandler.sockServ.getLocalPort()));
            String s3 = readLine();
            System.out.println("recv3 from " + endpointSock.getRemoteSocketAddress() + ": " + s3);
            StringBuilder s4 = new StringBuilder();
            for(int i = 0; i < 7; i++) {
                s4.append(s3);
            }
            writeLine(s4.toString());
            synchronized (serverHandler.fpLock) {
                writeLine(Long.toString(serverHandler.gValueGcd));
            }
            System.out.println(readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                endpointSock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
