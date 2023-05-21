import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
    private static String removeTrailingNewline(String sIn) {
        if(sIn.charAt(sIn.length() - 1) != '\n') {
            throw new IllegalArgumentException();
        }
        return sIn.substring(0, sIn.length() - 1);
    }

    public static void main(String[] args) throws IOException {
        DatagramSocket dsock = new DatagramSocket();
        int port = dsock.getLocalPort();

        Socket tsock = new Socket();
        tsock.connect(new InetSocketAddress("172.21.48.103", 16777));

        String addr = tsock.getLocalAddress().getHostAddress();

        String s = "111606\n" + addr + ":" + port + "\n";
        OutputStream os = tsock.getOutputStream();
        os.write(s.getBytes(StandardCharsets.UTF_8));

        byte[] data = new byte[1500];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        dsock.receive(packet);
        int len = packet.getLength();
        String req = new String(data, 0, len, StandardCharsets.UTF_8);
        req = removeTrailingNewline(req);

        {
            StringBuilder respb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                respb.append(req);
            }
            respb.append('\n');

            byte[] respdata = respb.toString().getBytes(StandardCharsets.UTF_8);
            DatagramPacket opacket = new DatagramPacket(respdata, respdata.length, packet.getSocketAddress());
            dsock.send(opacket);
        }

        {
            long sum = 0;
            for (int i = 0; i < 4; i++) {
                packet = new DatagramPacket(data, data.length);
                dsock.receive(packet);
                len = packet.getLength();
                String nstr = new String(data, 0, len, StandardCharsets.UTF_8);
                nstr = removeTrailingNewline(nstr);
                sum += Long.parseLong(nstr);
            }
            byte[] respdata = (sum + "\n").getBytes(StandardCharsets.UTF_8);
            DatagramPacket opacket = new DatagramPacket(respdata, respdata.length, packet.getSocketAddress());
            dsock.send(opacket);
        }

        {
            packet = new DatagramPacket(data, data.length);
            dsock.receive(packet);
            len = packet.getLength();
            String s1 = new String(data, 0, len, StandardCharsets.UTF_8);

            byte[] respdata = s1.replace("0", "").getBytes(StandardCharsets.UTF_8);
            DatagramPacket opacket = new DatagramPacket(respdata, respdata.length, packet.getSocketAddress());
            dsock.send(opacket);
        }

        packet = new DatagramPacket(data, data.length);
        dsock.receive(packet);
        len = packet.getLength();
        String exitToken = new String(data, 0, len, StandardCharsets.UTF_8);
        System.out.println(removeTrailingNewline(exitToken));
    }
}
