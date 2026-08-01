package views;

import controllers.menus.TravelLogController;
import enums.Commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestView {
    private static final Pattern SHOW_QUEST = Pattern.compile("^\\s*show\\s+quest\\s+-q\\s+(?<name>.+?)\\s*$", Pattern.CASE_INSENSITIVE);

    private QuestView() {
    }

    public static void check(String input) {
        if (input.matches(Commands.TRAVEL_LOG_PAGE.getPattern())) {
            System.out.println(TravelLogController.changePage(input));
        } else if (input.matches("(?i)^\\s*show\\s+quests\\s*$")) {
            System.out.println(TravelLogController.showQuests());
        } else {
            Matcher matcher = SHOW_QUEST.matcher(input);
            if (matcher.matches()) {
                System.out.println(TravelLogController.showQuest(matcher.group("name")));
            } else {
                System.out.println("invalid command");
            }
        }
    }
}
