package PvZ2.APproject.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class NetworkClient {

    private final String host;
    private final int port;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private final Map<String, CompletableFuture<Response>> pending = new ConcurrentHashMap<>();
    private volatile Consumer<Response> pushListener;

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public NetworkClient() {
        this("127.0.0.1", 5000);
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socket.getInputStream());
        Thread reader = new Thread(this::readLoop, "network-client-reader");
        reader.setDaemon(true);
        reader.start();
    }

    public CompletableFuture<Response> sendRequestAndGetResponse(Request request) throws IOException, ClassNotFoundException{
        CompletableFuture<Response> f = new CompletableFuture<>();
        pending.put(request.getRequestId(), f);
        synchronized (this) {
            output.writeObject(request);
            output.flush();
        }
        return f;
    }

    public Response sendAndWait(Request r) throws Exception {
        return sendRequestAndGetResponse(r).get(30, TimeUnit.SECONDS);
    }

    public void setPushListener(Consumer<Response> l) {
        pushListener = l;
    }

    private void readLoop() {
        try {
            while (!socket.isClosed()) {
                Response r = (Response) input.readObject();
                CompletableFuture<Response> f = pending.remove(r.getRequestId());
                if (f != null) f.complete(r);
                else if (pushListener != null) pushListener.accept(r);
            }
        } catch (Exception e) {
            pending.values().forEach(f -> f.completeExceptionally(e));
            pending.clear();
        }
    }

    public void disconnect(){
        try {
            if (socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
