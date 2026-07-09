package controllers;

import enums.QuestRelated.QuestData;
import models.App;
import models.Quest;
import models.QuestsModel;
import models.User;
import models.seasons.Season;

import java.util.ArrayList;

public class QuestController {
    public static QuestsModel questsModel = new QuestsModel();

    public void generateAllQuests() {
        for (QuestData questData : QuestData.values()) {
            Quest quest = new Quest(questData);
            questsModel.addQuest(quest);
        }
    }

    public void checkQuestObjectives() {}

    public static String claimReward(Quest quest, int amount) {
        User currentUser = App.getCurrentUser();
        switch (quest.getReward()) {
            case COIN -> {
                currentUser.setCoinsCount(currentUser.getCoinsCount() + amount);
                break;
            }
            case GEM -> {
                currentUser.setGemsCount(currentUser.getGemsCount() + amount);
                break;
            }
            case UNLOCKABLE -> {
                break;
            }
            case SEED_PACKET -> {
                //TODO: add to all seed packets count
                break;
            }
        }
        return null;
    }

    public void loadTemplatesFromCSV(String filePath) {}

    public void generateDailyQuests() {}

    public static void notifySunCollected(int value) {
        Quest dailySunCollector = questsModel.getQuestByName("Daily Sun Collector");
        dailySunCollector.setCurrentValue(dailySunCollector.getCurrentValue() + value);

        for (int i = 0; i < dailySunCollector.getTargetValue().length; i++) {
            if (dailySunCollector.getCurrentValue() == dailySunCollector.getTargetValue()[i]) {
                claimReward(dailySunCollector , dailySunCollector.getCurrentValue() / 100);
            }
        }
    }

    public static void notifyZombieKilled(Season season) {
        Quest ancientEgyptHunter = questsModel.getQuestByName("AncientEgypt Hunter");
        Quest frostbiteCavesHunter = questsModel.getQuestByName("FrostbiteCaves Hunter");
        Quest bigWaveBeachHunter = questsModel.getQuestByName("BigWaveBeach Hunter");
        Quest darkAgesHunter = questsModel.getQuestByName("DarkAges Hunter");

        ArrayList<Quest> chapterQuests = new ArrayList<>();
        chapterQuests.add(ancientEgyptHunter);
        chapterQuests.add(frostbiteCavesHunter);
        chapterQuests.add(bigWaveBeachHunter);
        chapterQuests.add(darkAgesHunter);

        switch (season.getName()) {
            case "AncientEgypt" :
                ancientEgyptHunter.setCurrentValue(ancientEgyptHunter.getCurrentValue() + 1);
                break;
            case "FrostbiteCaves" :
                frostbiteCavesHunter.setCurrentValue(frostbiteCavesHunter.getCurrentValue() + 1);
                break;
            case "BigWaveBeach" :
                bigWaveBeachHunter.setCurrentValue(bigWaveBeachHunter.getCurrentValue() + 1);
                break;
            case "DarkAges" :
                darkAgesHunter.setCurrentValue(darkAgesHunter.getCurrentValue() + 1);
                break;
        }

        for (Quest quest : chapterQuests) {
            if (quest.getCurrentValue() == quest.getTargetValue()[0]) {
                claimReward(quest, quest.getRewardAmount());
            }
        }
    }

    public static void notifyNoSunsLeft() {
        Quest defenseMaster = questsModel.getQuestByName("Defense Master");
        claimReward(defenseMaster, defenseMaster.getRewardAmount());
    }
}
