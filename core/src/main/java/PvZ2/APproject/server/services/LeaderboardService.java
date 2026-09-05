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
        Comparator<ServerUser> comparator = switch (sort.toLowerCase()) {
            case "last level" -> Comparator.comparingInt(ServerUser::getLastSeasonId)
                .thenComparingInt(ServerUser::getLastLevelNumber);
            case "minigames" -> Comparator.comparingInt(ServerUser::getMinigamesWon);
            case "daily quests" -> Comparator.comparingInt(ServerUser::getCompletedDailyQuests);
            case "quests", "non-daily quests" -> Comparator.comparingInt(ServerUser::getCompletedQuests);
            case "games" -> Comparator.comparingInt(ServerUser::getGamesPlayed);
            default -> Comparator.comparingInt(ServerUser::getHighestPoint);
        };
        comparator = comparator.thenComparing(ServerUser::getUsername, String.CASE_INSENSITIVE_ORDER);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        list.sort(comparator);
        StringBuilder b = new StringBuilder();
        int rank = 1;
        for (ServerUser user : list) {
            b.append(rank++).append('|')
                .append(escape(user.getUsername())).append('|')
                .append(user.getLastSeasonId()).append('|')
                .append(user.getLastLevelNumber()).append('|')
                .append(user.getMinigamesWon()).append('|')
                .append(user.getCompletedDailyQuests()).append('|')
                .append(user.getCompletedQuests()).append('|')
                .append(user.getHighestPoint()).append('|')
                .append(user.getGamesPlayed()).append('\n');
        }
        return new Response(request.getRequestId(), MessageType.GET_LEADERBOARD, true,
            "Leaderboard", Map.of("entries", b.toString()));
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|").replace("\n", "\\n");
    }

}
