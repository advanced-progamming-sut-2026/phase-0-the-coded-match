package PvZ2.APproject.views;

import PvZ2.APproject.controllers.menus.TravelLogController;

public class QuestView {
    public static void check(String input) {
        if (input.matches("^\\s*show\\s+quests\\s*$")) {
            System.out.println(TravelLogController.showQuests());
        } else if (input.matches("^\\s*claim\\s+quest\\s+-q\\s+.+$")) {
            String name = input.replaceFirst("^\\s*claim\\s+quest\\s+-q\\s+", "").trim();
            System.out.println(TravelLogController.claimQuestReward(name));
        } else if (input.matches("^\\s*refresh\\s+daily\\s+quests\\s*$")) {
            System.out.println(TravelLogController.refreshDailyQuests());
        } else {
            System.out.println("invalid command");
        }
    }
}
