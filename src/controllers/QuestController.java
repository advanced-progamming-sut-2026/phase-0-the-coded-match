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
import models.zombies.Barrel;

import java.util.*;

public class QuestController {
    public static QuestsModel questsModel = new QuestsModel();

    private static Set<String> plantsThatKilledZombies = new HashSet<>();

    public static void generateAllQuests() {
        if (!questsModel.getAvailableQuests().isEmpty()) { // if the user is loaded from json
            return;
        }
        for (QuestData questData : QuestData.values()) {
            Quest quest = new Quest(questData);
            if (quest.getQuestData().isNeedsPlant()) {
                Plant targetPlant = null;
                switch (quest.getQuestName()) {

                    case "Pro Plant Player":
                        targetPlant = getRandomPlant("killer");
                        break;
                    case "Only Cactus":
                        targetPlant = getRandomPlant("cactus");
                        break;
                }

                if (targetPlant != null) {
                    quest.setTargetPlant(targetPlant);
                }
            }
            questsModel.addQuest(quest);
            App.getCurrentUser().setQuestsModel(questsModel);
        }
    }

    public static void refreshDailyQuests() {
        //todo: check current time with the time daily quests were generated
    }

    public static Plant getRandomPlant(String type) {
        List<Plant> unlockedPlants = App.getCurrentUser().getCollection().getAvailablePlants();
        PlantCategory plantCategory;

        if (type.equalsIgnoreCase("killer")) {
            List<Plant> killerPlants = new ArrayList<>();

            for (Plant plant : unlockedPlants) {
                plantCategory = plant.getData().getCategory();
                if (plantCategory == PlantCategory.SHOOTER || plantCategory == PlantCategory.LOBBER ||
                        plantCategory == PlantCategory.EXPLOSIVE || plantCategory == PlantCategory.MELEE ||
                        plantCategory == PlantCategory.STRIKE_TROUGH) {
                    killerPlants.add(plant);
                }
            }

            if (!killerPlants.isEmpty()) {
                Random random = new Random();
                int randomIndex = random.nextInt(killerPlants.size());

                return killerPlants.get(randomIndex);
            } else if (type.equalsIgnoreCase("cactus")) {
                Plant plant = App.getPlantByName("Cactus");
                return plant;
            }
        }

        return null;
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
    } // Explanation: a daily quest that is used to see if the player has used 3 explosive type of plants in the current game/level

    //Has to somehow check the level is done
    public static void onLevelCompleted(boolean levelWon){
        Quest symmetry = questsModel.getQuestByName("Symmetry");
        if(GameManagerController.getInstance().getCurrentLevel().getGameMap().checkGardenSymmetry()) {
            claimReward(symmetry, symmetry.getRewardAmount());
        }
    }// Explanation: a daily quest that is used to see at the end of the game the garden is symmetrical meaning if the same amount and type of plants are on parallel rows

    public static void onZombieDefeated(String killerPlantFamily){
        plantsThatKilledZombies.add(killerPlantFamily);

        Quest proPlantPlayer = questsModel.getQuestByName("Pro Plant Player");
        if (killerPlantFamily.equalsIgnoreCase(proPlantPlayer.getTargetPlant().getData().getName())) {
            proPlantPlayer.setCurrentValue(proPlantPlayer.getCurrentValue() + 1);
        }

         isItDone(proPlantPlayer);

        Quest onlyCactus = questsModel.getQuestByName("Only Cactus");
        if (killerPlantFamily.equalsIgnoreCase("cactus")) {
            onlyCactus.setCurrentValue(onlyCactus.getCurrentValue() + 1);
        }

        isItDone(onlyCactus);
    }

    public static void isItDone(Quest quest) {
        for (int i = 0; i < quest.getTargetValue().length; i++) {
            if (quest.getCurrentValue() == quest.getTargetValue()[i]) {
                claimReward(quest, quest.getRewardAmount());
            }
        }
    }
}
