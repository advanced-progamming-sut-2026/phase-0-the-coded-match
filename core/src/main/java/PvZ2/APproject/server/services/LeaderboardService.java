package PvZ2.APproject.server.services;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.server.ClientHandler;
import PvZ2.APproject.server.models.ServerUser;
import PvZ2.APproject.server.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class LeaderboardService {
    private final UserRepository users;

    public LeaderboardService(UserRepository users) {
        this.users = users;
    }

    public Response get(Request request, ClientHandler handler){
        String sort = request.get("sort");
        if(sort == null){
            sort = "score";
        }
        boolean ascending = "ascending".equalsIgnoreCase(request.get("order"));
        List<ServerUser> list = new ArrayList<>(users.all());
        Comparator<ServerUser> c = switch (sort.toLowerCase()) {
            case "minigames" -> Comparator.comparingInt(ServerUser::getMinigamesWon);
            case "games" -> Comparator.comparingInt(ServerUser::getGamesPlayed);
            default -> Comparator.comparingInt(ServerUser::getHighestPoint);
        };
        if (!ascending) {
            c = c.reversed();
        }
        list.sort(c);
        StringBuilder b = new StringBuilder();
        int rank = 1;
        for (ServerUser user : list) {
            b.append(rank++).append("|").append(user.getUsername()).append("|").append(user.getHighestPoint()).append("|").append(user.getMinigamesWon()).append("|").append(user.getGamesPlayed()).append("\n");
        }
        return new Response(request.getRequestId(), MessageType.GET_LEADERBOARD, true, "Leaderboard", Map.of("entries", b.toString()));
    }

}
