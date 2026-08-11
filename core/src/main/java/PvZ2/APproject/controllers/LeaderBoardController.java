package PvZ2.APproject.controllers;

import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeaderBoardController {

    public static StringBuilder sortUsers(String input, StringBuilder sb) {
        List<User> users;
        users = getSortedUsers(input);
        if (users == null || users.isEmpty()) {
            sb.append("No users found in leaderboard.\n");
            return sb;
        }
        for (User user : users) {
            sb.append(user.getUsername()).append(": ").append("\n");
            sb.append("season ").append(user.getLastSeason() == null ? 0 : user.getLastSeason().getData().getId()).append(" - level ").append(user.getLastLevel() == null ? 0 : user.getLastLevel().getLevelNumber()).append("\n");
            sb.append("minigames won:").append(user.getMinigamesWonCount()).append("\n");
            sb.append("quests done: ").append(user.getCompletedQuestsCount()).append(" - daily quests done: ").append(user.getCompletedDailyQuestsCount()).append("\n");
            sb.append("highest point: ").append(user.getHighestPointAchieved()).append("\n");
            sb.append("=============================\n");
        }
        return sb;
    }

    public static List<User> getSortedUsers(String input) {
        Pattern pattern = Pattern.compile(Commands.SORT_LEADERBOARD.getPattern());
        Matcher matcher = pattern.matcher(input);
        List<User> allUsers = new ArrayList<>(App.getUsers());

        if (!matcher.matches()) {
            allUsers.sort(Comparator.comparingInt(User::getHighestPointAchieved).reversed());
            return allUsers;
        }

        String sortBy = matcher.group(1);
        String isAscendingSt = matcher.group(2);
        boolean isAscending;
        if (isAscendingSt.equalsIgnoreCase("true")) {
            isAscending = true;
        } else {
            isAscending = false;
        }
        Comparator<User> comparator;

        switch (sortBy) {
            case "last level":
                comparator = Comparator.comparingInt((User user) -> user.getLastSeason() == null ? 0 : user.getLastSeason().getData().getId())
                        .thenComparingInt(user -> user.getLastLevel() == null ? 0 : user.getLastLevel().getLevelNumber());
                break;

            case "minigames":
                comparator = Comparator.comparingInt(User::getMinigamesWonCount);
                break;

            case "daily quests":
                comparator = Comparator.comparingInt(User::getCompletedDailyQuestsCount);
                break;

            case "quests":
            case "non-daily quests":
                comparator = Comparator.comparingInt(User::getCompletedQuestsCount);
                break;

            case "score":
            default:
                comparator = Comparator.comparingInt(User::getHighestPointAchieved);
                break;
        }
        if (!isAscending) {
            comparator = comparator.reversed();
        }
        allUsers.sort(comparator);
        return allUsers;
    }
}
