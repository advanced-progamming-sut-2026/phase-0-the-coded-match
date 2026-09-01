package PvZ2.APproject.server.services;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.server.ClientHandler;
import PvZ2.APproject.server.models.GameSession;

import java.util.Map;
import java.util.Set;

public class ReactionService {
    private static final Set<String> TEXT = Set.of("Good luck!", "Nice move!", "Well played!");
    private static final Set<String> EMOJI = Set.of("😀", "😎", "😂");
    private static final Set<String> STICKER = Set.of("sticker1", "sticker2", "sticker3");
    private final MatchmakingService matchmaking;

    public ReactionService(MatchmakingService m) {
        matchmaking = m;
    }


    public Response send(Request request, ClientHandler handler) {
        GameSession s = matchmaking.session(handler.getSessionId());
        if (s == null) {
            return Response.error(request.getRequestId(), "You are not in a game");
        }
        String kind = request.get("kind"), value = request.get("value");
        if (kind == null || value == null) {
            return Response.error(request.getRequestId(), "Missing reaction");
        }
        Set<String> allowed = switch (kind.toUpperCase()) {
            case "TEXT" -> TEXT;
            case "EMOJI" -> EMOJI;
            case "STICKER" -> STICKER;
            default -> Set.of();
        };
        if (!allowed.contains(value)) {
            return Response.error(request.getRequestId(), "Invalid reaction");
        }
        ClientHandler opponent = s.opponentOf(handler.getUsername());
        opponent.push(new Response(request.getRequestId(), MessageType.REACTION_RECEIVED, true, "Reaction received", Map.of("kind", kind, "value", value, "from", handler.getUsername())));
        return Response.ok(request.getRequestId(), MessageType.SEND_REACTION, "Reaction sent");
    }
}
