package PvZ2.APproject.server.services;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.server.ClientHandler;
import PvZ2.APproject.server.models.GameAction;
import PvZ2.APproject.server.models.GameSession;
import PvZ2.APproject.server.models.PlayerRole;

import java.util.Map;
import java.util.UUID;

public class GameService {
    private final MatchmakingService matchmaking;

    public GameService(MatchmakingService matchmaking) {
        this.matchmaking = matchmaking;
    }

    public Response action(Request request, ClientHandler handler) {
        GameSession session = requireSession(handler, request);
        if (session == null) {
            return Response.error(request.getRequestId(), "You are not in a game");
        }
        if (session.getState().isFinished()) {
            finishByTimeout(session);
            return Response.error(request.getRequestId(), "Game is already over");
        }
        String actionName = request.get("action");
        if (actionName == null || actionName.isBlank()) return Response.error(request.getRequestId(), "Missing action");

        PlayerRole role = roleOf(session, handler);
        String requestedRole = request.get("role");
        if (requestedRole != null && !requestedRole.equalsIgnoreCase(role.name())) {
            return Response.error(request.getRequestId(), "You cannot control the opponent");
        }

        String normalized = actionName.trim().toUpperCase();
        if (!isAllowed(normalized, role, request)) {
            return Response.error(request.getRequestId(), "Invalid action for your role");
        }

        String entityId = request.get("entity");
        if (entityId == null || entityId.isBlank()) entityId = UUID.randomUUID().toString();
        GameAction action = new GameAction(
            handler.getUsername(), role, normalized,
            entityId, request.get("entityType"),
            parseDouble(request.get("x"), 0), parseDouble(request.get("y"), 0));
        session.getState().addAction(action);
        broadcastState(session);
        return Response.ok(request.getRequestId(), MessageType.GAME_ACTION, "Action accepted");
    }

    public Response state(Request request, ClientHandler handler) {
        GameSession session = requireSession(handler, request);
        if (session == null) return Response.error(request.getRequestId(), "Game session not found");
        if (session.getState().isFinished()) finishByTimeout(session);
        return new Response(request.getRequestId(), MessageType.GAME_STATE, true, "Game state", session.getState().snapshot());
    }

    public Response finish(Request request, ClientHandler handler) {
        GameSession session = requireSession(handler, request);
        if (session == null) return Response.error(request.getRequestId(), "Game session not found");
        if (!session.contains(handler.getUsername())) return Response.error(request.getRequestId(), "Not a player");
        if (session.getState().isFinished()) {
            finishByTimeout(session);
            return new Response(request.getRequestId(), MessageType.GAME_OVER, true, "Game already finished", session.getState().snapshot());
        }

        String reason = request.get("reason");
        String requestedWinner = request.get("winner");
        String winner;
        if (System.currentTimeMillis() - session.getState().getStartedAt() >= 120_000) {
            winner = session.getState().getPlantPlayer();
        } else if ("brains_eaten".equalsIgnoreCase(reason)
            && session.getState().getZombiePlayer().equalsIgnoreCase(requestedWinner)) {
            winner = session.getState().getZombiePlayer();
        } else if ("defended".equalsIgnoreCase(reason)
            && session.getState().getPlantPlayer().equalsIgnoreCase(requestedWinner)) {
            winner = session.getState().getPlantPlayer();
        } else {
            return Response.error(request.getRequestId(), "Game result is not valid yet");
        }

        session.getState().finish(winner);
        broadcastGameOver(session);
        return new Response(request.getRequestId(), MessageType.GAME_OVER, true, "Game finished", session.getState().snapshot());
    }

    private boolean isAllowed(String action, PlayerRole role, Request request) {
        if (action.equals("PLACE_ZOMBIE")) {
            if (role != PlayerRole.ZOMBIES) return false;
            double x = parseDouble(request.get("x"), -1);
            double y = parseDouble(request.get("y"), -1);
            return x >= 6 && x <= 9 && y >= 1 && y <= 5 && !blank(request.get("entityType"));
        }
        if (action.equals("PLACE_PLANT")) {
            if (role != PlayerRole.PLANTS) return false;
            double x = parseDouble(request.get("x"), -1);
            double y = parseDouble(request.get("y"), -1);
            return x >= 1 && x <= 5 && y >= 1 && y <= 5 && !blank(request.get("entityType"));
        }
        if (action.equals("REMOVE_ENTITY") || action.equals("MOVE_ENTITY") || action.equals("PROJECTILE")) {
            return !blank(request.get("entity"));
        }
        return false;
    }

    private GameSession requireSession(ClientHandler handler, Request request) {
        if (handler.getUsername() == null || handler.getSessionId() == null) return null;
        return matchmaking.session(handler.getSessionId());
    }

    private PlayerRole roleOf(GameSession session, ClientHandler handler) {
        return session.getPlayerA().equalsIgnoreCase(handler.getUsername()) ? PlayerRole.PLANTS : PlayerRole.ZOMBIES;
    }

    private void finishByTimeout(GameSession session) {
        if (session.getState().getWinner() != null) return;
        if (System.currentTimeMillis() - session.getState().getStartedAt() < session.getState().getDurationMillis()) return;
        session.getState().finish(session.getState().getPlantPlayer());
        broadcastGameOver(session);
    }

    private void broadcastState(GameSession session) {
        Map<String, String> data = session.getState().snapshot();
        session.handlerFor(session.getPlayerA()).push(new Response(session.getId(), MessageType.GAME_STATE, true, "Game state", data));
        session.handlerFor(session.getPlayerB()).push(new Response(session.getId(), MessageType.GAME_STATE, true, "Game state", data));
    }

    private void broadcastGameOver(GameSession session) {
        Map<String, String> data = session.getState().snapshot();
        session.handlerFor(session.getPlayerA()).push(new Response(session.getId(), MessageType.GAME_OVER, true, "Game over", data));
        session.handlerFor(session.getPlayerB()).push(new Response(session.getId(), MessageType.GAME_OVER, true, "Game over", data));
    }

    private double parseDouble(String value, double fallback) {
        try { return Double.parseDouble(value); } catch (Exception e) { return fallback; }
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }
}

