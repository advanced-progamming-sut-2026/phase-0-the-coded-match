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
        if (isConnected()) return;
        socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socket.getInputStream());
        Thread reader = new Thread(this::readLoop, "network-client-reader");
        reader.setDaemon(true);
        reader.start();
    }

    public CompletableFuture<Response> sendRequestAndGetResponse(Request request) throws IOException, ClassNotFoundException{
        if (request == null) throw new IllegalArgumentException("request cannot be null");
        if (!isConnected()) throw new IOException("Not connected to server");

        CompletableFuture<Response> future = new CompletableFuture<>();
        pending.put(request.getRequestId(), future);
        try {
            synchronized (this) {
                output.writeObject(request);
                output.flush();
                output.reset();
            }
        } catch (IOException e) {
            pending.remove(request.getRequestId());
            future.completeExceptionally(e);
            throw e;
        }
        return future;
    }

    public Response sendAndWait(Request r) throws Exception {
        return sendRequestAndGetResponse(r).get(30, TimeUnit.SECONDS);
    }

    public void setPushListener(Consumer<Response> l) {
        pushListener = l;
    }

    private void readLoop() {
        Exception failure = new IOException("Connection to server closed");
        try {
            while (isConnected()) {
                Object object = input.readObject();
                if (!(object instanceof Response response)) continue;
                CompletableFuture<Response> future = pending.remove(response.getRequestId());
                if (future != null) future.complete(response);
                else if (pushListener != null) {
                    try { pushListener.accept(response); }
                    catch (RuntimeException callbackError) { callbackError.printStackTrace(); }
                }
            }
        } catch (Exception e) {
            failure = e;
        } finally {
            final Exception finalFailure = failure;
            pending.values().forEach(f -> f.completeExceptionally(finalFailure));
            pending.clear();
        }
    }

    public void disconnect(){
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {
        } finally {
            socket = null;
            input = null;
            output = null;
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
