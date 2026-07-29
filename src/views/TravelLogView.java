package views;

import controllers.menus.TravelLogController;
import enums.Commands;

public class TravelLogView {
    public static void check(String input) {
        if (input.matches(Commands.TRAVEL_LOG_PAGE.getPattern())) {
            System.out.println(TravelLogController.changePage(input));
        } else if (input.matches("^\\s*show\\s+quests\\s*$")) {
            System.out.println(TravelLogController.showQuests().toString());
        } else if (input.matches("^\\s*show\\s+minigames\\s*$")) {
            System.out.println(TravelLogController.showMinigames().toString());
        } else {
            System.out.println("invalid command");
        }
    }
}
