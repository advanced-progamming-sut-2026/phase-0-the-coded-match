package PvZ2.APproject.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String requestId;
    private final MessageType type;
    private final boolean success;
    private final String message;
    private final Map<String, String> data;


    public Response(String requestId, MessageType type, boolean success, String message) {
        this(requestId, type, success, message, new HashMap<>());
    }

    public Response(String requestId, MessageType type, boolean success, String message, Map<String, String> data) {
        this.requestId = requestId;
        this.type = type;
        this.success = success;
        this.message = message;
        this.data = data == null ? new HashMap<>() : new HashMap<>(data);
    }

    public String getRequestId() {
        return requestId;
    }

    public MessageType getType(){
        return type;
    }

    public String getMessage(){
        return message;
    }

    public boolean isSuccess(){
        return success;
    }

    public Map<String, String> getData() {
        return data;
    }

    public String get(String key) {
        return data.get(key);
    }

    public static Response ok(String id, MessageType type, String message) {
        return new Response(id, type, true, message);
    }

    public static Response error(String id, String message) {
        return new Response(id, MessageType.ERROR, false, message);
    }
}
