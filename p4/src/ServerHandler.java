import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicLong;

public class ServerHandler {
    public ServerSocket sockServ;

    public long gValueGcd = 0L;
    public long gValueSum = 0L;
    public final Object fpLock = new Object();

    public ServerHandler() {
    }

    public void initializeServer() throws IOException {
        sockServ = new ServerSocket(0);

        new ListenThread(this).start();
    }
}
