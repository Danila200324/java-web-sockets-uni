import java.io.IOException;
import java.net.Socket;

class ListenThread extends Thread {
    private final ServerHandler nodeInstance;

    public ListenThread(ServerHandler nodeInstance) {
        this.nodeInstance = nodeInstance;

        setName("Node listen thread");
    }

    @Override
    public void run() {
        while (!nodeInstance.sockServ.isClosed()) {
            try {
                Socket endpointSock = nodeInstance.sockServ.accept();
                new ClientEndpointThread(nodeInstance, endpointSock).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
