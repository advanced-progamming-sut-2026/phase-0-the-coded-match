package controllers.menus;

import enums.Commands;
import enums.Menu;
import models.App;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TravelLogController {
    public static String changePage(String input) {
        Pattern pattern = Pattern.compile(Commands.TRAVEL_LOG_PAGE.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return "invalid command";
        }

        String page = matcher.group("page");
        page = page.toLowerCase();
        switch (page) {
            case "quests":
                App.setCurrentMenu(Menu.QUESTS);
                return "entered quests";
            case "minigames":
                App.setCurrentMenu(Menu.MINIGAMES);
                return "entered minigames";
            default:
                return "invalid page name";
        }
    }
}
