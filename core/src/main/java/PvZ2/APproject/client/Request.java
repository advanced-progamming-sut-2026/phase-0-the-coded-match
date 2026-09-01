package PvZ2.APproject.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String requestId;
    private final MessageType type;
    private final Map<String, String> data;


    public Request(MessageType type) {
        this(type, new HashMap<>());
    }

    public Request(MessageType type, Map<String, String> data){
        this.requestId = UUID.randomUUID().toString();
        this.type = type;
        this.data = data == null ? new HashMap<>() : new HashMap<>(data);
    }

    public MessageType getType(){
        return type;
    }

    public Map<String, String> getData() {
        return data;
    }

    public String get(String key) {
        return data.get(key);
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String getRequestId() {
        return requestId;
    }


}
