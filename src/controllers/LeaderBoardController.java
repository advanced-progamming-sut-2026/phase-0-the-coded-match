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
    public static StringBuilder showUsers(StringBuilder builder) {
        return appendUsers(new ArrayList<>(App.getUsers()), builder);
    }

    public static StringBuilder sortUsers(String input, StringBuilder builder) {
        List<User> users = getSortedUsers(input);
        if (users == null) {
            return builder.append("invalid sort column or order");
        }
        return appendUsers(users, builder);
    }

    private static StringBuilder appendUsers(List<User> users, StringBuilder builder) {
        for (User user : users) {
            builder.append(user.getUsername()).append(":\n");
            int seasonNumber = user.getLastSeason() == null ? 0 : user.getLastSeason().getData().getId();
            int levelNumber = user.getLastLevel() == null ? 0 : user.getLastLevel().getLevelNumber();
            builder.append("season ").append(seasonNumber).append(" - level ").append(levelNumber).append('\n');
            builder.append("minigames won: ").append(user.getMinigamesWonCount()).append('\n');
            builder.append("daily quests done: ").append(user.getCompletedDailyQuestsCount())
                    .append(" - non-daily quests done: ").append(user.getCompletedNonDailyQuestsCount()).append('\n');
            builder.append("highest point: ").append(user.getHighestPointAchieved()).append('\n');
            builder.append("=============================\n");
        }
        return builder;
    }

    public static List<User> getSortedUsers(String input) {
        Matcher matcher = Pattern.compile(Commands.SORT_LEADERBOARD.getPattern(), Pattern.CASE_INSENSITIVE).matcher(input);
        if (!matcher.matches()) {
            return null;
        }
        String sortBy = matcher.group(1).trim().toLowerCase();
        String order = matcher.group(2).trim().toLowerCase();
        if (!order.equals("true") && !order.equals("false")) {
            return null;
        }
        Comparator<User> comparator = switch (sortBy) {
            case "last level" -> Comparator.comparingInt(LeaderBoardController::seasonNumber)
                    .thenComparingInt(LeaderBoardController::levelNumber);
            case "minigames" -> Comparator.comparingInt(User::getMinigamesWonCount);
            case "daily quests" -> Comparator.comparingInt(User::getCompletedDailyQuestsCount);
            case "quests", "non-daily quests" -> Comparator.comparingInt(User::getCompletedNonDailyQuestsCount);
            case "score" -> Comparator.comparingInt(User::getHighestPointAchieved);
            default -> null;
        };
        if (comparator == null) {
            return null;
        }
        if (order.equals("false")) {
            comparator = comparator.reversed();
        }
        List<User> users = new ArrayList<>(App.getUsers());
        users.sort(comparator.thenComparing(User::getUsername, String.CASE_INSENSITIVE_ORDER));
        return users;
    }

    private static int seasonNumber(User user) {
        return user.getLastSeason() == null ? 0 : user.getLastSeason().getData().getId();
    }

    private static int levelNumber(User user) {
        return user.getLastLevel() == null ? 0 : user.getLastLevel().getLevelNumber();
    }
}
