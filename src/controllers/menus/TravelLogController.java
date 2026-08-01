package controllers.menus;

import controllers.QuestController;
import enums.Commands;
import enums.Menu;
import enums.QuestRelated.QuestCategory;
import enums.QuestRelated.QuestData;
import enums.QuestRelated.RewardType;
import models.App;
import models.Quest;
import models.QuestsModel;
import models.plants.PlantData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TravelLogController {
    private static String questPage = "all";

    private TravelLogController() {
    }

    public static String changePage(String input) {
        Matcher matcher = Pattern.compile(Commands.TRAVEL_LOG_PAGE.getPattern(), Pattern.CASE_INSENSITIVE).matcher(input);
        if (!matcher.matches()) {
            return "invalid command";
        }
        String page = matcher.group("page").toLowerCase(Locale.ROOT);
        if (page.equals("minigames")) {
            App.setCurrentMenu(Menu.MINIGAMES);
            return "entered minigames";
        }
        switch (page) {
            case "quests", "all" -> questPage = "all";
            case "daily" -> questPage = "daily";
            case "main", "story" -> questPage = "main";
            case "challenge", "epic" -> questPage = "challenge";
            case "completed" -> questPage = "completed";
            default -> {
                return "invalid page name";
            }
        }
        QuestController.initializeForCurrentUser();
        App.setCurrentMenu(Menu.QUESTS);
        return "entered " + questPage + " quests";
    }

    public static StringBuilder showQuests() {
        QuestController.initializeForCurrentUser();
        StringBuilder result = new StringBuilder();
        if (App.getCurrentUser() == null) {
            return result.append("no user is logged in");
        }
        List<Quest> quests = getCurrentPageQuests(App.getCurrentUser().getQuestsModel());
        result.append("--- ").append(questPage).append(" quests: ").append(quests.size()).append(" ---\n");
        if (quests.isEmpty()) {
            return result.append("no quests found");
        }
        for (Quest quest : quests) {
            appendQuest(result, quest);
        }
        return result;
    }

    public static String showQuest(String questName) {
        QuestController.initializeForCurrentUser();
        if (App.getCurrentUser() == null) {
            return "no user is logged in";
        }
        Quest quest = App.getCurrentUser().getQuestsModel().getQuestByName(questName);
        if (quest == null) {
            return "quest does not exist";
        }
        StringBuilder result = new StringBuilder();
        appendQuest(result, quest);
        return result.toString();
    }

    private static List<Quest> getCurrentPageQuests(QuestsModel model) {
        return switch (questPage) {
            case "daily" -> model.getQuestsByCategory(QuestCategory.DAILY);
            case "main" -> model.getQuestsByCategory(QuestCategory.MAIN);
            case "challenge" -> model.getQuestsByCategory(QuestCategory.CHALLENGE);
            case "completed" -> model.getCompletedQuests();
            default -> new ArrayList<>(model.getAvailableQuests());
        };
    }

    private static void appendQuest(StringBuilder result, Quest quest) {
        result.append("name: ").append(quest.getQuestName()).append('\n');
        result.append("category: ").append(quest.getCategory().name().toLowerCase(Locale.ROOT)).append('\n');
        result.append("priority: ").append(quest.getPriority().name().toLowerCase(Locale.ROOT)).append('\n');
        result.append("status: ").append(quest.isCompleted() ? "completed" : "active").append('\n');
        result.append("condition: ").append(formatCondition(quest)).append('\n');
        result.append("progress: ").append(formatProgress(quest)).append('\n');
        result.append("reward: ").append(formatReward(quest)).append('\n');
        result.append("---------------------------\n");
    }

    private static String formatCondition(Quest quest) {
        String condition = quest.getMissionDescription();
        int target = quest.getSelectedTarget();
        condition = condition.replace("sun_amount", Integer.toString(target));
        condition = condition.replaceAll("\\bn\\b", Integer.toString(target));
        PlantData targetPlant = quest.getTargetPlant();
        if (targetPlant != null) {
            condition = condition.replace("Plant", displayName(targetPlant));
            if (targetPlant.getCategory() != null) {
                condition = condition.replace("family_type", targetPlant.getCategory().getName());
            }
        }
        if (quest.getQuestData() == QuestData.CHAPTER_HUNTER) {
            String season = quest.getTargetSeasonName() == null ? "selected" : quest.getTargetSeasonName();
            condition = condition.replace("chapter season", season + " chapter");
        }
        return condition;
    }

    private static String formatProgress(Quest quest) {
        if (quest.isCompleted()) {
            return "completed";
        }
        return switch (quest.getQuestData()) {
            case DAILY_SUN_COLLECTOR, CHAPTER_HUNTER, PRO_PLANT_PLAYER, ONLY_CACTUS,
                    SPEED_EXECUTION, PROFESSIONAL_DEMOLISHER, WIN_STREAK,
                    ALMOST_VICTORIOUS, MOWING_TIME ->
                    quest.getCurrentValue() + "/" + quest.getSelectedTarget();
            case ECONOMIC_HERBIVORE ->
                    "lost " + quest.getCurrentValue() + ", maximum " + quest.getSelectedTarget();
            case FAMILY_SLAUGHTER -> quest.getCurrentValue() + " matching kills";
            default -> "pending";
        };
    }

    private static String formatReward(Quest quest) {
        int amount = quest.getRewardAmount();
        if (quest.getQuestData() == QuestData.DAILY_SUN_COLLECTOR) {
            amount = quest.getSelectedTarget() / 100;
        } else if (quest.getQuestData() == QuestData.ECONOMIC_HERBIVORE) {
            amount = Math.max(0, 20 - quest.getSelectedTarget());
        } else if (quest.getQuestData() == QuestData.MOWING_TIME) {
            amount = quest.getSelectedTarget();
        }
        RewardType type = quest.getReward();
        return switch (type) {
            case COIN -> amount + " coins";
            case GEM -> amount + " gems";
            case UNLOCKABLE -> "one random locked plant";
            case SEED_PACKET -> {
                PlantData plant = quest.getRewardPlant();
                if (plant == null) {
                    yield amount + " seed packets for a random available plant";
                }
                yield amount + " seed packets for " + displayName(plant);
            }
        };
    }

    private static String displayName(PlantData plant) {
        return plant.getDisplayName() == null || plant.getDisplayName().isBlank()
                ? plant.getName() : plant.getDisplayName();
    }

    public static StringBuilder showMinigames() {
        return new StringBuilder("--- MiniGames ---\n")
                .append("Vasebreaker - Wallnut Bowling - I, Zombie - Beghouled - Zombotany");
    }
}
