package controllers;

import enums.Commands;
import models.App;
import models.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeaderBoardController {

    public static StringBuilder sortUsers(String input, StringBuilder sb) {
        List<User> users;
        users = getSortedUsers(input);
        for (User user : users) {
            sb.append(user.getUsername()).append(": ").append("\n");
            sb.append("season ").append(user.getLastSeason()).append(" - level ").append(user.getLastLevel()).append("\n");;//Todo: get the numbers
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

        if (!matcher.matches()) {
            return null;
        }

        String sortBy = matcher.group(1);
        String isAscendingSt = matcher.group(2);
        boolean isAscending;
        if (isAscendingSt.equalsIgnoreCase("true")) {
            isAscending = true;
        } else {
            isAscending = false;
        }

        List<User> allUsers = App.getUsers();
        Comparator<User> comparator = null;

        switch (sortBy) {
            case "last level":
                comparator = Comparator.comparingInt((User user) -> user.getLastSeason().getData().getId())
                        .thenComparingInt((User user) -> user.getLastLevel().getLevelNumber());
                break;

            case "minigames":
                comparator = Comparator.comparingInt(User::getMinigamesWonCount);
                break;

            case "daily quests":
                comparator = Comparator.comparingInt(User::getCompletedDailyQuestsCount);
                break;

            case "quests":
                comparator = Comparator.comparingInt(User::getCompletedQuestsCount);
                break;

            case "score":
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
