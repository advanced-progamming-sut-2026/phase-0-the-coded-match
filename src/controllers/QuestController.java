package controllers;

import enums.PlantCategory;
import enums.PlantTag;
import enums.QuestRelated.QuestData;
import models.App;
import models.Quest;
import models.QuestsModel;
import models.User;
import models.plants.Plant;
import models.seasons.Season;

import java.util.ArrayList;
import java.util.Random;

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

    public static void notifyPlantsDestroyed(int count) {
        Quest economicHerbivore = questsModel.getQuestByName("Economic Herbivore");
        economicHerbivore.setCurrentValue(count);
        for (int i = 0; i < economicHerbivore.getTargetValue().length; i++) {
            if (economicHerbivore.getCurrentValue() == economicHerbivore.getTargetValue()[i]) {
                claimReward(economicHerbivore, 20 - economicHerbivore.getCurrentValue());
                return;
            }
        }
        economicHerbivore.setCurrentValue(0);
    }

    public static void onPlantPlaced(Plant plant){
        Quest professionalDemolisher = questsModel.getQuestByName("Professional Demolisher");
       if(plant.hasThisTag(PlantTag.EXPLOSIVE)){
           professionalDemolisher.setCurrentValue(professionalDemolisher.getCurrentValue()+1);
       }

       if(professionalDemolisher.getCurrentValue() == professionalDemolisher.getTargetValue()[0]){
           claimReward(professionalDemolisher, professionalDemolisher.getRewardAmount());
       }
    }

    //Has to somehow check the level is done
    public static void onLevelCompleted(boolean levelWon){
        Quest symmetry = questsModel.getQuestByName("Symmetry");
        if(GameManagerController.getInstance().getCurrentLevel().getGameMap().checkGardenSymmetry()) {
            claimReward(symmetry, symmetry.getRewardAmount());
        }
    }


}
