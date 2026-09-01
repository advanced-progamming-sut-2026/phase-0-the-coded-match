package PvZ2.APproject.controllers;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.models.App;

import java.util.function.Consumer;

public final class MatchmakingController {
    private static Consumer<Response> invitationListener;
    private static Response pendingInvitation;

    private MatchmakingController() {}

    public static String findRandom(int stage) { return send(MessageType.FIND_RANDOM_MATCH, stage, null); }
    public static String findPlayer(String username, int stage) { return send(MessageType.FIND_PLAYER, stage, username); }

    public static String accept(String invitationId) { return simple(MessageType.ACCEPT_MATCH, "invitationId", invitationId); }
    public static String reject(String invitationId) { return simple(MessageType.REJECT_MATCH, "invitationId", invitationId); }

    public static synchronized void setInvitationListener(Consumer<Response> listener) {
        invitationListener = listener;
        if (listener != null && pendingInvitation != null) {
            Response pending = pendingInvitation;
            pendingInvitation = null;
            listener.accept(pending);
        }
    }

    public static synchronized void clearInvitationListener() { invitationListener = null; }

    public static synchronized void handleIncoming(Response response) {
        if (response == null || response.getType() != MessageType.MATCH_INVITATION) return;
        if (invitationListener != null) invitationListener.accept(response);
        else pendingInvitation = response;
    }

    private static String send(MessageType type, int stage, String username) {
        if (App.getCurrentUser() == null || !App.getNetworkClient().isConnected()) return "Error: server is not connected";
        try {
            Request request = new Request(type);
            request.put("stage", String.valueOf(Math.max(1, Math.min(3, stage))));
            if (username != null) request.put("username", username.trim());
            Response response = App.getNetworkClient().sendAndWait(request);
            return response.getMessage();
        } catch (Exception e) { return "Error: could not contact server"; }
    }

    private static String simple(MessageType type, String key, String value) {
        if (value == null || value.isBlank()) return "Invalid request";
        try {
            Request request = new Request(type);
            request.put(key, value);
            Response response = App.getNetworkClient().sendAndWait(request);
            return response.getMessage();
        } catch (Exception e) { return "Error: could not contact server"; }
    }

}
