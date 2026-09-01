package PvZ2.APproject.server.services;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.server.ClientHandler;
import PvZ2.APproject.server.Server;
import PvZ2.APproject.server.models.GameSession;
import PvZ2.APproject.server.repositories.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MatchmakingService {
    private final UserRepository users;
    private final Queue<QueuedPlayer> randomQueue = new ArrayDeque<>();
    private final Map<String, PendingInvite> invites = new ConcurrentHashMap<>();
    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    public MatchmakingService(UserRepository users) {
        this.users = users;
    }

    public Response random(Request request, ClientHandler handler) {
        if (handler.getUsername() == null){
            return Response.error(request.getRequestId(), "Login required");
        }
        int stage = parseStage(request.get("stage"));
        synchronized (lock) {
            randomQueue.removeIf(x -> x.handler.getUsername() == null || x.handler == handler);
            for (QueuedPlayer queued : randomQueue) {
                if (queued.stage == stage) {
                    randomQueue.remove(queued);
                    createSession(handler, queued.handler, stage);
                    return Response.ok(request.getRequestId(), MessageType.MATCH_FOUND, "Match found");
                }
            }
            randomQueue.add(new QueuedPlayer(handler, stage));
            return Response.ok(request.getRequestId(), MessageType.FIND_RANDOM_MATCH, "Added to matchmaking queue");
        }
    }

    public Response findPlayer(Request request, ClientHandler handler) {
        String target = request.get("username");
        if (handler.getUsername() == null) {
            return Response.error(request.getRequestId(), "Login required");
        }
        if (target == null || target.equalsIgnoreCase(handler.getUsername())) {
            return Response.error(request.getRequestId(), "Invalid target");
        }
        if (users.find(target) == null) {
            return Response.error(request.getRequestId(), "User does not exist");
        }
        ClientHandler targetHandler = Server.online(target);
        if (targetHandler == null) {
            return Response.error(request.getRequestId(), "User is offline");
        }
        String id = request.getRequestId();
        int stage = parseStage(request.get("stage"));
        invites.put(id, new PendingInvite(handler, targetHandler, stage));
//        Map<String, String> d = Map.of("invitationId", id, "from", handler.getUsername());
//        targetHandler.push(new Response(id, MessageType.MATCH_INVITATION, true, "Game invitation from " + handler.getUsername(), d));
//        return new Response(id, MessageType.FIND_PLAYER, true, "Invitation sent", Map.of("invitationId", id));
        Map<String, String> data = Map.of("invitationId", id, "from", handler.getUsername(), "stage", String.valueOf(stage));
        targetHandler.push(new Response(id, MessageType.MATCH_INVITATION, true,
            "Game invitation from " + handler.getUsername(), data));
        return new Response(id, MessageType.FIND_PLAYER, true, "Invitation sent", Map.of("invitationId", id));
    }

    public Response accept(Request request, ClientHandler handler) {
        PendingInvite invite = invites.remove(request.get("invitationId"));
        if (invite == null || invite.target != handler) return Response.error(request.getRequestId(), "Invitation not found");
        if (invite.from.getUsername() == null) return Response.error(request.getRequestId(), "Inviter is no longer online");
        createSession(invite.from, invite.target, invite.stage);
        return Response.ok(request.getRequestId(), MessageType.MATCH_FOUND, "Match accepted");
    }

    public Response reject(Request request, ClientHandler handler) {
        PendingInvite invite = invites.remove(request.get("invitationId"));
        if (invite == null || invite.target != handler) return Response.error(request.getRequestId(), "Invitation not found");
        invite.from.push(new Response(request.getRequestId(), MessageType.REJECT_MATCH, true, "Game invitation rejected"));
        return Response.ok(request.getRequestId(), MessageType.REJECT_MATCH, "Invitation rejected");
    }

    private void createSession(ClientHandler a, ClientHandler b, int stage) {
        GameSession session = new GameSession(a.getUsername(), b.getUsername(), stage, a, b);
        sessions.put(session.getId(), session);
        a.setSessionId(session.getId());
        b.setSessionId(session.getId());

        Map<String, String> da = new HashMap<>();
        da.put("sessionId", session.getId());
        da.put("role", "PLANTS");
        da.put("opponent", b.getUsername());
        da.put("stage", String.valueOf(stage));
        Map<String, String> db = new HashMap<>();
        db.put("sessionId", session.getId());
        db.put("role", "ZOMBIES");
        db.put("opponent", a.getUsername());
        db.put("stage", String.valueOf(stage));

        a.push(new Response(session.getId(), MessageType.MATCH_FOUND, true, "Match found", da));
        b.push(new Response(session.getId(), MessageType.MATCH_FOUND, true, "Match found", db));
    }

    public void disconnect(ClientHandler handler) {
        synchronized (lock) { randomQueue.removeIf(q -> q.handler == handler); }
        invites.entrySet().removeIf(e -> e.getValue().from == handler || e.getValue().target == handler);
        String sessionId = handler.getSessionId();
        if (sessionId != null) {
            GameSession session = sessions.remove(sessionId);
            if (session != null) {
                ClientHandler opponent = session.opponentOf(handler.getUsername());
                if (opponent != null && opponent.getUsername() != null) {
                    opponent.setSessionId(null);
                    opponent.push(new Response(session.getId(), MessageType.GAME_OVER, true,
                        "Opponent disconnected", Map.of("winner", opponent.getUsername(), "reason", "opponent_disconnected")));
                }
            }
        }
    }

    private int parseStage(String value) {
        try { return Math.max(1, Math.min(3, Integer.parseInt(value))); }
        catch (Exception e) { return 1; }
    }

    public GameSession session(String id) {
        return sessions.get(id);
    }

    public void remove(String id) {
        sessions.remove(id);
    }

    public Collection<GameSession> sessions() {
        return sessions.values();
    }

    private record QueuedPlayer(ClientHandler handler, int stage) {}

    private record PendingInvite(ClientHandler from, ClientHandler target, int stage) {
    }
}
