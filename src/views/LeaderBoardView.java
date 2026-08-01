package views;

import controllers.LeaderBoardController;
import enums.Commands;

public class LeaderBoardView {
    public static void check(String input) {
        StringBuilder builder = new StringBuilder();
        if (input.matches(Commands.SHOW_LEADERBOARD.getPattern())) {
            LeaderBoardController.showUsers(builder);
            System.out.print(builder);
        } else if (input.matches(Commands.SORT_LEADERBOARD.getPattern())) {
            LeaderBoardController.sortUsers(input, builder);
            System.out.print(builder);
        } else {
            System.out.println("invalid command");
        }
    }
}
