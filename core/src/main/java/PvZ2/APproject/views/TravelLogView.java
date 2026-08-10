package views;

import controllers.menus.TravelLogController;
import enums.Commands;

public class TravelLogView {
    public static void check(String input) {
        if (input.matches(Commands.TRAVEL_LOG_PAGE.getPattern())) {
            System.out.println(TravelLogController.changePage(input));
        } else if (input.matches("^\\s*show\\s+quests\\s*$")) {
            System.out.println(TravelLogController.showQuests());
        } else if (input.matches("^\\s*show\\s+quest\\s+-q\\s+.+$")) {
            String name = input.replaceFirst("^\\s*show\\s+quest\\s+-q\\s+", "").trim();
            System.out.println(TravelLogController.showQuest(name));
        } else if (input.matches("^\\s*claim\\s+quest\\s+-q\\s+.+$")) {
            String name = input.replaceFirst("^\\s*claim\\s+quest\\s+-q\\s+", "").trim();
            System.out.println(TravelLogController.claimQuestReward(name));
        } else if (input.matches("^\\s*refresh\\s+daily\\s+quests\\s*$")) {
            System.out.println(TravelLogController.refreshDailyQuests());
        } else if (input.matches("^\\s*show\\s+minigames\\s*$")) {
            System.out.println(TravelLogController.showMinigames());
        } else {
            System.out.println("invalid command");
        }
    }
}
