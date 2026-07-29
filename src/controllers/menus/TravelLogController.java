package controllers.menus;

import enums.Commands;
import enums.Menu;
import models.App;
import models.Quest;

import java.util.List;
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
    public static StringBuilder showQuests() {
        StringBuilder sb = new StringBuilder();
        List<Quest> quests = App.getCurrentUser().getQuestsModel().getAvailableQuests();
        sb.append("--- Quests ---").append("\n");
        for (Quest quest : quests) {
            sb.append("name: ").append(quest.getQuestName()).append("\n");
            sb.append("condition: ").append(quest.getQuestData().getConditionText()).append("\n");
            sb.append("progression: ").append(quest.getCurrentValue()).append("\n");
            sb.append("---------------------------");
        }
        return sb;
    }
    public static StringBuilder showMinigames() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- MiniGames ---").append("\n");
        sb.append("Vasebreaker - Wallnut Bowling - I, Zombie - Beghouled - Zombotany");
        return sb;
    }
}
