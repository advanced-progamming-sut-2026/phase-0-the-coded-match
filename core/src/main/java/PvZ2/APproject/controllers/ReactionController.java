package PvZ2.APproject.controllers;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.models.App;

import java.util.function.Consumer;

public class ReactionController {
    private static Consumer<Response> incomingListener;
    private ReactionController() {}

    public static String sendText(String value) { return send("TEXT", value); }
    public static String sendEmoji(String value) { return send("EMOJI", value); }
    public static String sendSticker(String value) { return send("STICKER", value); }

    public static String send(String kind, String value) {
        if (App.getCurrentUser() == null || !App.getNetworkClient().isConnected()) return "Error: server is not connected";
        try {
            Request request = new Request(MessageType.SEND_REACTION);
            request.put("kind", kind);
            request.put("value", value);
            Response response = App.getNetworkClient().sendAndWait(request);
            return response.getMessage();
        } catch (Exception e) { return "Error: could not contact server"; }
    }

    public static synchronized void setIncomingListener(Consumer<Response> listener) { incomingListener = listener; }
    public static synchronized void clearIncomingListener() { incomingListener = null; }

    public static synchronized void handleIncoming(Response response) {
        if (response == null || response.getType() != MessageType.REACTION_RECEIVED) return;
        if ("STICKER".equalsIgnoreCase(response.get("kind"))) {
            BonusGameController.addStickerPoints(response.get("value"));
        }
        if (incomingListener != null) incomingListener.accept(response);
        else System.out.println("Reaction from " + response.get("from") + ": " + response.get("value"));
    }
}
