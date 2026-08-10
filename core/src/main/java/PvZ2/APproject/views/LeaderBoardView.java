package views;

import controllers.LeaderBoardController;
import enums.Commands;

public class LeaderBoardView {
    public static StringBuilder stringBuilder = new StringBuilder();

    public static void check(String input) {
        if (input.matches("^\\s*show\\s+leaderboard\\s*$")) {
            LeaderBoardController.sortUsers("", stringBuilder);
            System.out.println(stringBuilder.toString());
            stringBuilder.setLength(0);
        } else if (input.matches(Commands.SORT_LEADERBOARD.getPattern())) {
            LeaderBoardController.sortUsers(input, stringBuilder);
            System.out.println(stringBuilder.toString());
            stringBuilder.delete(0, stringBuilder.length());
        } else {
            System.out.println("invalid command");
        }
    }

}
