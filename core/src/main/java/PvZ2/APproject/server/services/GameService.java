package PvZ2.APproject.server.services;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.server.ClientHandler;
import PvZ2.APproject.server.models.GameSession;

import java.util.Map;

public class GameService {
    private final MatchmakingService matchmaking;

    public GameService(MatchmakingService matchmaking) {
        this.matchmaking = matchmaking;
    }

    public Response action(Request request, ClientHandler handler) {
        if (handler.getSessionId() == null) {
            return Response.error(request.getRequestId(), "You are not in a game");
        }
        GameSession s = matchmaking.session(handler.getSessionId());
        if (s == null) {
            return Response.error(request.getRequestId(), "Game session not found");
        }
        if (s.getState().isFinished()) {
            return Response.error(request.getRequestId(), "Game is already over");
        }
        String action = request.get("action");
        if (action == null || action.isBlank()) {
            return Response.error(request.getRequestId(), "Missing action");
        }
        String role = handler.getUsername().equalsIgnoreCase(s.getPlayerA()) ? "PLANTS" : "ZOMBIES";
        String requestedRole = request.get("role");
        if (requestedRole != null && !requestedRole.equalsIgnoreCase(role)) {
            return Response.error(request.getRequestId(), "You cannot control the opponent");
        }
        s.getState().addAction(handler.getUsername() + "|" + role + "|" + action + "|" + request.get("x") + "|" + request.get("y") + "|" + request.get("entity"));
        broadcastState(s);
        return Response.ok(request.getRequestId(), MessageType.GAME_ACTION, "Action accepted");
    }

    public Response state(Request request, ClientHandler handler) {
        GameSession s = matchmaking.session(handler.getSessionId());
        if (s == null) {
            return Response.error(request.getRequestId(), "Game session not found");
        }
        return new Response(request.getRequestId(), MessageType.GAME_STATE, true, "Game state", s.getState().snapshot());
    }

    public Response finish(Request request, ClientHandler handler) {
        GameSession s = matchmaking.session(handler.getSessionId());
        if (s == null) {
            return Response.error(request.getRequestId(), "Game session not found");
        }
        String winner = request.get("winner");
        if (!handler.getUsername().equalsIgnoreCase(s.getPlayerA()) && !handler.getUsername().equalsIgnoreCase(s.getPlayerB())) {
            return Response.error(request.getRequestId(), "Not a player");
        }
        s.getState().finish(winner);
        broadcastState(s);
        return new Response(request.getRequestId(), MessageType.GAME_OVER, true, "Game finished", s.getState().snapshot());
    }

    private void broadcastState(GameSession s) {
        Map<String, String> d = s.getState().snapshot();
        s.handlerFor(s.getPlayerA()).push(new Response(s.getId(), MessageType.GAME_STATE, true, "Game state", d));
        s.handlerFor(s.getPlayerB()).push(new Response(s.getId(), MessageType.GAME_STATE, true, "Game state", d));
    }
}
