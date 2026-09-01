package PvZ2.APproject.server.services;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.server.ClientHandler;
import PvZ2.APproject.server.models.ServerUser;
import PvZ2.APproject.server.repositories.UserRepository;

public class ScoreService {
    private final UserRepository users;

    public ScoreService(UserRepository users) {
        this.users = users;
    }

    public Response submit(Request request, ClientHandler handler) {
        if (handler.getUsername() == null) {
            return Response.error(request.getRequestId(), "Login required");
        }
        int score;
        try {
            score = Integer.parseInt(request.get("score"));
        } catch (Exception e) {
            return Response.error(request.getRequestId(), "Invalid score");
        }
        if (score < 0) {
            return Response.error(request.getRequestId(), "Score cannot be negative");
        }
        ServerUser user = users.find(handler.getUsername());
        int old = user.getHighestPoint();
        user.setHighestPoint(score);
        users.update(user);
        return Response.ok(request.getRequestId(), MessageType.SUBMIT_SCORE, score > old ? "New record" : "Score recorded");
    }
}
