package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.controllers.QuestController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.enums.QuestRelated.QuestCategory;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Quest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TravelLogController {
    private static String currentPage = "all";

    public static String changePage(String input) {
        Pattern pattern = Pattern.compile(Commands.TRAVEL_LOG_PAGE.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) return "invalid command";
        String page = matcher.group("page").toLowerCase();
        switch (page) {
            case "daily":
            case "main":
            case "challenge":
            case "completed":
            case "all":
                currentPage = page;
                return "entered " + page + " quests";
            case "quests":
                currentPage = "all";
                App.setCurrentMenu(Menu.QUESTS);
                return "entered quests";
            case "minigames":
                App.setCurrentMenu(Menu.MINIGAMES);
                return "entered minigames\n" + showMinigames();
            default:
                return "invalid page name";
        }
    }

    public static StringBuilder showQuests() {
        StringBuilder sb = new StringBuilder("--- Quests ---\n");
        List<Quest> quests = App.getCurrentUser().getQuestsModel().getAvailableQuests();
        for (Quest quest : quests) {
            if (!matchesPage(quest)) continue;
            sb.append("name: ").append(quest.getQuestName()).append('\n');
            sb.append("condition: ").append(formatCondition(quest)).append('\n');
            sb.append("progression: ").append(quest.getCurrentValue());
            if (quest.getTargetValue() != null && quest.getTargetValue().length > 0) {
                sb.append('/').append(quest.getTargetValue()[0]);
            }
            sb.append('\n');
            sb.append("status: ").append(quest.isCompleted() ? (quest.isRewardClaimed() ? "claimed" : "completed") : "active").append('\n');
            sb.append("---------------------------\n");
        }
        return sb;
    }

    private static boolean matchesPage(Quest quest) {
        if (currentPage.equals("all")) return true;
        if (currentPage.equals("completed")) return quest.isCompleted();
        QuestCategory category = quest.getQuestData().getCategory();
        return category != null && category.name().equalsIgnoreCase(currentPage);
    }

    private static String formatCondition(Quest quest) {
        String condition = quest.getQuestData().getConditionText();
        int[] targets = quest.getTargetValue();
        if (targets != null && targets.length > 0) {
            condition = condition.replace("sun_amount", String.valueOf(targets[0]));
            condition = condition.replaceAll("\\bn\\b", String.valueOf(targets[0]));
        }
        if (quest.getTargetPlant() != null) condition = condition.replace("family_type", quest.getTargetPlant().getCategory().name());
        return condition;
    }

    public static String showQuest(String name) {
        for (Quest quest : App.getCurrentUser().getQuestsModel().getAvailableQuests()) {
            if (quest.getQuestName().equalsIgnoreCase(name)) {
                String status = quest.isCompleted() ? (quest.isRewardClaimed() ? "claimed" : "completed") : "active";
                return "name: " + quest.getQuestName() + "\ncondition: " + formatCondition(quest) + "\nprogression: " + quest.getCurrentValue() + "\nstatus: " + status;
            }
        }
        return "quest not found";
    }

    public static String claimQuestReward(String name) {
        return QuestController.claimReward(name);
    }

    public static String refreshDailyQuests() {
        QuestController.refreshDailyQuests(true);
        return "daily quests refreshed";
    }

    public static StringBuilder showMinigames() {
        return new StringBuilder("--- MiniGames ---\nVasebreaker - Wallnut Bowling - I, Zombie - Beghouled - Zombotany");
    }
}
