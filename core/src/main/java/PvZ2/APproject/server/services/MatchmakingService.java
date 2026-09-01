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
    private final Queue<ClientHandler> randomQueue = new ArrayDeque<>();
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
        synchronized (lock) {
            randomQueue.removeIf(x -> x.getUsername() == null || x == handler);
            if (!randomQueue.isEmpty()) {
                ClientHandler other = randomQueue.iterator().next();
                randomQueue.remove(other);
                createSession(handler, other);
                return Response.ok(request.getRequestId(), MessageType.MATCH_FOUND, "Match found");
            }
            randomQueue.add(handler);
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
        invites.put(id, new PendingInvite(handler, targetHandler));
        Map<String, String> d = Map.of("invitationId", id, "from", handler.getUsername());
        targetHandler.push(new Response(id, MessageType.MATCH_INVITATION, true, "Game invitation from " + handler.getUsername(), d));
        return new Response(id, MessageType.FIND_PLAYER, true, "Invitation sent", Map.of("invitationId", id));
    }

    public Response accept(Request request, ClientHandler handler) {
        PendingInvite person = invites.remove(request.get("invitationId"));
        if (person == null || person.target != handler) {
            return Response.error(request.getRequestId(), "Invitation not found");
        }
        createSession(person.from, person.target);
        return Response.ok(request.getRequestId(), MessageType.MATCH_FOUND, "Match accepted");
    }

    public Response reject(Request request, ClientHandler handler) {
        PendingInvite person = invites.remove(request.get("invitationId"));
        if (person == null || person.target != handler) {
            return Response.error(request.getRequestId(), "Invitation not found");
        }
        person.from.push(new Response(request.getRequestId(), MessageType.REJECT_MATCH, true, "Game invitation rejected"));
        return Response.ok(request.getRequestId(), MessageType.REJECT_MATCH, "Invitation rejected");
    }

    private void createSession(ClientHandler a, ClientHandler b) {
        GameSession s = new GameSession(a.getUsername(), b.getUsername(), a, b);
        sessions.put(s.getId(), s);
        a.setSessionId(s.getId());
        b.setSessionId(s.getId());
        Map<String, String> da = new HashMap<>();
        da.put("sessionId", s.getId());
        da.put("role", "PLANTS");
        da.put("opponent", b.getUsername());
        Map<String, String> db = new HashMap<>();
        db.put("sessionId", s.getId());
        db.put("role", "ZOMBIES");
        db.put("opponent", a.getUsername());
        a.push(new Response(s.getId(), MessageType.MATCH_FOUND, true, "Match found", da));
        b.push(new Response(s.getId(), MessageType.MATCH_FOUND, true, "Match found", db));
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

    private record PendingInvite(ClientHandler from, ClientHandler target) {
    }
}
