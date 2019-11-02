package srv;

import api.MessageEncoderDecoder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private ConnectedClientsTPC<T> connectedClientsTPC;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private AtomicInteger nextClientId;


    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
		this.nextClientId=new AtomicInteger(0);
    }

    @Override
    public void serve() {

        connectedClientsTPC = ConnectedClientsTPC.getInstance();
        try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();
                BidiMessagingProtocol<T>bidProtocol=protocolFactory.get();//creates new protocol for the client
                int currId=nextClientId.incrementAndGet();
                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler(
                        clientSock,
                        encdecFactory.get(),
                        bidProtocol);
                System.out.println("open socket"+ currId);

                connectedClientsTPC.addConnectedClient(nextClientId.get(),handler);
                bidProtocol.start(currId, connectedClientsTPC);
                execute(handler);
            }
        } catch (IOException ex) {
        }
        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
        {
            sock.close();
        }
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}
