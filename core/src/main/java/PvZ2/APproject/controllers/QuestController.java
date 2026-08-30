package PvZ2.APproject.controllers;

import PvZ2.APproject.enums.PlantCategory;
import PvZ2.APproject.enums.PlantTag;
import PvZ2.APproject.enums.QuestRelated.QuestData;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Quest;
import PvZ2.APproject.models.QuestsModel;
import PvZ2.APproject.models.User;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.seasons.Season;
import PvZ2.APproject.models.zombies.Zombie;

import java.time.LocalDate;
import java.util.*;

import static PvZ2.APproject.enums.QuestRelated.QuestCategory.DAILY;

public class QuestController {
    public static QuestsModel questsModel = new QuestsModel();

    private static Set<String> plantsThatKilledZombies = new HashSet<>();
    private static int explosivePlantsPlacedThisLevel = 0;
    private static boolean brokeFamilyRule = false;
    private static boolean usedForbiddenFamily = false;
    private static boolean notMushrooms = false;
    private static int winCount;
    private static int numberOfZombiesKilled;
    private static int sunProducerPlantsPlacedThisLevel;
    private static boolean cloudyDayRuleBroken;

    private static void useCurrentUserQuests() {
        if (App.getCurrentUser() != null) {
            questsModel = App.getCurrentUser().getQuestsModel();
            if (questsModel.getAvailableQuests() == null) {
                questsModel.setAvailableQuests(new ArrayList<>());
            }
        }
    }

    public static void generateAllQuests() {
        useCurrentUserQuests();
        if (!questsModel.getAvailableQuests().isEmpty()) {
            return;
        }
        for (QuestData questData : QuestData.values()) {
            questsModel.addQuest(createQuest(questData));
        }
        App.getCurrentUser().setQuestsModel(questsModel);
    }

    private static Quest createQuest(QuestData questData) {
        Quest quest = new Quest(questData);
        Random random = new Random();
        switch (questData) {
            case ONE_COLUMN_LESS -> quest.setTargetValue(new int[]{1 + random.nextInt(9)});
            case DEFENSELESS_ROW, DEFENSELESS_CROSS ->
                    quest.setTargetValue(new int[]{1 + random.nextInt(5)});
            case MOWING_TIME -> {
                int[] possibleTargets = questData.getTargetValue();
                quest.setTargetValue(new int[]{possibleTargets[random.nextInt(possibleTargets.length)]});
            }
        }
        if (questData.isNeedsPlant()) {
            PlantData targetPlant = switch (quest.getQuestName()) {
                case "Pro Plant Player" -> getRandomPlant("killer");
                case "Only Cactus" -> getRandomPlant("cactus");
                case "Family Slaughter", "Blooming in Limits" -> getRandomPlant();
                default -> null;
            };
            if (targetPlant != null) {
                quest.setTargetPlant(targetPlant);
            }
        }
        return quest;
    }

    public static void refreshDailyQuests() {
        refreshDailyQuests(false);
    }

    public static void refreshDailyQuests(boolean force) {
        useCurrentUserQuests();
        String today = LocalDate.now().toString();
        if (!force && today.equals(App.getCurrentUser().getLastDailyQuestRefreshDate())) {
            return;
        }
        List<Quest> refreshed = new ArrayList<>();
        for (Quest quest : questsModel.getAvailableQuests()) {
            if (quest.getQuestData().getCategory() != DAILY) {
                refreshed.add(quest);
            }
        }
        for (QuestData questData : QuestData.values()) {
            if (questData.getCategory() == DAILY) {
                refreshed.add(createQuest(questData));
            }
        }
        questsModel.setAvailableQuests(refreshed);
        App.getCurrentUser().setQuestsModel(questsModel);
        App.getCurrentUser().setLastDailyQuestRefreshDate(today);
        onLevelStarted();
    }

    public static void onLevelStarted() {
        useCurrentUserQuests();
        explosivePlantsPlacedThisLevel = 0;
        brokeFamilyRule = false;
        usedForbiddenFamily = false;
        notMushrooms = false;
        sunProducerPlantsPlacedThisLevel = 0;
        cloudyDayRuleBroken = false;
        numberOfZombiesKilled = 0;

    }

    public static PlantData getRandomPlant(String type) {
        List<String> unlockedPlants = App.getCurrentUser().getCollection().getAvailablePlantsIds();
        PlantCategory plantCategory;

        if (type.equalsIgnoreCase("killer")) {
            List<PlantData> killerPlants = new ArrayList<>();

            for (String plantId : unlockedPlants) {
                PlantData plant = PlantRepository.getInstance().findById(plantId);
                plantCategory = plant.getCategory();
                if (plantCategory == PlantCategory.SHOOTER || plantCategory == PlantCategory.LOBBER ||
                        plantCategory == PlantCategory.EXPLOSIVE || plantCategory == PlantCategory.MELEE ||
                        plantCategory == PlantCategory.STRIKE_TROUGH) {
                    killerPlants.add(plant);
                }
            }

            if (!killerPlants.isEmpty()) {
                int randomIndex = new Random().nextInt(killerPlants.size());
                return killerPlants.get(randomIndex);

            }

        } else if (type.equalsIgnoreCase("cactus")) {
            PlantData plant = PlantRepository.getInstance().findByName("Cactus");
            return plant;
        }

        return null;
    }

    public static PlantData getRandomPlant(){
        List<String> unlockedPlants = App.getCurrentUser().getCollection().getAvailablePlantsIds();
        if (unlockedPlants.isEmpty()) return PlantRepository.getInstance().getAllPlants().get(0);
        int randomIndex = new Random().nextInt(unlockedPlants.size());
        return PlantRepository.getInstance().findById(unlockedPlants.get(randomIndex));
    }

    public void checkQuestObjectives() {
        useCurrentUserQuests();
        for (Quest quest : questsModel.getAvailableQuests()) {
            if (quest.isCompleted() || quest.getTargetValue() == null) {
                continue;
            }
            for (int target : quest.getTargetValue()) {
                if (target > 0 && quest.getCurrentValue() >= target) {
                    completeQuest(quest, quest.getRewardAmount());
                    break;
                }
            }
        }
    }

    public static void completeQuest(Quest quest, int amount) {
        if (quest == null || quest.isCompleted()) {
            return;
        }
        quest.setCompleted(true);
        quest.setPendingRewardAmount(amount);
    }

    public static String claimReward(Quest quest, int amount) {
        if (quest == null) {
            return "quest not found";
        }
        if (!quest.isCompleted()) {
            return "quest is not completed";
        }
        if (quest.isRewardClaimed()) {
            return "reward already claimed";
        }
        User currentUser = App.getCurrentUser();
        if (currentUser == null) {
            return "no logged in user";
        }
        int rewardAmount = amount >= 0 ? amount : quest.getPendingRewardAmount();
        switch (quest.getReward()) {
            case COIN -> currentUser.setCoinsCount(currentUser.getCoinsCount() + Math.max(0, rewardAmount));
            case GEM -> currentUser.setGemsCount(currentUser.getGemsCount() + Math.max(0, rewardAmount));
            case UNLOCKABLE -> {
                PlantData rewardPlant = getRewardPlant();
                if (rewardPlant != null) {
                    currentUser.getCollection().unlockPlant(rewardPlant.getId());
                }
            }
            case SEED_PACKET -> {
                PlantData rewardPlant = quest.getTargetPlant() == null ? getRandomPlant() : quest.getTargetPlant();
                if (rewardPlant != null) {
                    currentUser.addSeedPackets(rewardPlant.getName(), Math.max(0, rewardAmount));
                }
            }
        }
        quest.setRewardClaimed(true);
        return "reward claimed";
    }

    public static String claimReward(String questName) {
        useCurrentUserQuests();
        Quest quest = questsModel.getQuestByName(questName);
        return claimReward(quest, quest == null ? 0 : quest.getPendingRewardAmount());
    }

    public static PlantData getRewardPlant() {
        List<PlantData> lockedPlants = App.getLockedPlants();
        if (lockedPlants != null && !lockedPlants.isEmpty()) {
            int randomIndex = new Random().nextInt(lockedPlants.size());
            return lockedPlants.get(randomIndex);
        }
        return null;
    }

    public void loadTemplatesFromCSV(String filePath) {}

    public static void notifySunCollected(int value) {
        useCurrentUserQuests();
        Quest dailySunCollector = questsModel.getQuestByName("Daily Sun Collector");
        if (dailySunCollector == null) return;
        dailySunCollector.setCurrentValue(dailySunCollector.getCurrentValue() + value);

        if (!dailySunCollector.isCompleted()
                && dailySunCollector.getCurrentValue() >= dailySunCollector.getTargetValue()[0]) {
            completeQuest(dailySunCollector, dailySunCollector.getTargetValue()[0] / 100);
        }
    }

    public static void notifyZombieKilled(Season season) {
        useCurrentUserQuests();
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
                completeQuest(quest, quest.getRewardAmount());
            }
        }
        if (GameManagerController.getInstance().getCurrentLevel().getZombieWave().getCurrentWave() == 0) {
            Quest speedExecution = questsModel.getQuestByName("Speed Execution");

            double currentTick = GameManagerController.getInstance().getCurrentLevel().getCurrentTick();
            double timeWaveStarted = GameManagerController.getInstance().getCurrentLevel().getZombieWave().getTimeWaveStarted();

            if (currentTick - timeWaveStarted <= 300) {
                speedExecution.setCurrentValue(speedExecution.getCurrentValue() + 1);
            }

            isItDone(speedExecution);
        }

    }

    public static void notifyZombieKilled(Zombie zombie){
        int col = (int) zombie.getX();
        int row = zombie.getY();
        if(col != 1 || GameManagerController.getInstance().getCurrentLevel().getGameMap().lawnMowerUsed(row) != null){
            return;
        }
        Quest almostVictorious = questsModel.getQuestByName("Almost Victorious");
        numberOfZombiesKilled += 1;
        if(numberOfZombiesKilled >= 10){
            numberOfZombiesKilled =0;
            completeQuest(almostVictorious, almostVictorious.getRewardAmount());
        }
    }

    public static void notifyNoSunsLeft() {
        Quest defenseMaster = questsModel.getQuestByName("Defense Master");
        completeQuest(defenseMaster, defenseMaster.getRewardAmount());
    }

    public static void notifyPlantsDestroyed(int count) {
        Quest economicHerbivore = questsModel.getQuestByName("Economic Herbivore");
        economicHerbivore.setCurrentValue(count);
        for (int i = 0; i < economicHerbivore.getTargetValue().length; i++) {
            if (economicHerbivore.getCurrentValue() == economicHerbivore.getTargetValue()[i]) {
                completeQuest(economicHerbivore, 20 - economicHerbivore.getCurrentValue());
                return;
            }
        }
        economicHerbivore.setCurrentValue(0);
    }

    public static void onPlantPlaced(Plant plant){
        Quest professionalDemolisher = questsModel.getQuestByName("Professional Demolisher");
       if(plant.hasThisTag(PlantTag.EXPLOSIVE)){
           explosivePlantsPlacedThisLevel += 1;
           professionalDemolisher.setCurrentValue(explosivePlantsPlacedThisLevel);
       }
       if(professionalDemolisher.getCurrentValue() == professionalDemolisher.getTargetValue()[0]){
           completeQuest(professionalDemolisher, professionalDemolisher.getRewardAmount());}

       Quest bloomingInLimits = questsModel.getQuestByName("Blooming in Limits");
       if(plant.getData().getCategory() == bloomingInLimits.getTargetPlant().getCategory()){
           usedForbiddenFamily = true;
       }

       Quest nightOrMorning =questsModel.getQuestByName("Night or Morning");
       if(GameManagerController.getInstance().getCurrentLevel().isDay()){
           if(!plant.hasThisTag(PlantTag.SHROOM)){
               notMushrooms = true;
           }
       }

       if (plant.getData().getAbilities() != null &&
               plant.getData().getAbilities().contains("PRODUCE_SUN")) {
           sunProducerPlantsPlacedThisLevel++;
       } else {
           cloudyDayRuleBroken = true;
       }
    }

    public static void onLevelCompleted(boolean levelWon){
        Quest symmetry = questsModel.getQuestByName("Symmetry");
        if(GameManagerController.getInstance().getCurrentLevel().getGameMap().checkGardenSymmetry()) {
            completeQuest(symmetry, symmetry.getRewardAmount());
        }

        Quest familySlaughter = questsModel.getQuestByName("Family Slaughter");
        if(!brokeFamilyRule){
            completeQuest(familySlaughter, familySlaughter.getRewardAmount());
        }

        Quest bloomingInLimits = questsModel.getQuestByName("Blooming in Limits");
        if(!usedForbiddenFamily){
            completeQuest(bloomingInLimits, bloomingInLimits.getRewardAmount());
        }

        Quest nightOrMorning =questsModel.getQuestByName("Night or Morning");
        if(GameManagerController.getInstance().getCurrentLevel().isDay()){
            if(!notMushrooms){
                completeQuest(nightOrMorning, nightOrMorning.getRewardAmount());
            }
        }

        Quest winStreak = questsModel.getQuestByName("Win Streak");
        if(App.getCurrentUser().isVictroy() && GameManagerController.getInstance().getCurrentLevel().getLevelDifficulty() == 5){
            winCount += 1;
            if(winCount >= 5){
                winCount = 0;
                completeQuest(winStreak, winStreak.getRewardAmount());
            }
        }else{
            winCount = 0;
        }

        checkRemainingLevelCompletionQuests(levelWon);
        onLevelStarted();
    }

    private static void checkRemainingLevelCompletionQuests(boolean levelWon) {
        if (!levelWon) {
            return;
        }

        Quest noOcd = questsModel.getQuestByName("No OCD");
        if (hasNoMirroredPlantPairs()) {
            completeQuest(noOcd, noOcd.getRewardAmount());
        }

        Quest cloudyDay = questsModel.getQuestByName("Cloudy Day");
        if (!cloudyDayRuleBroken && sunProducerPlantsPlacedThisLevel == cloudyDay.getTargetValue()[0]) {
            completeQuest(cloudyDay, cloudyDay.getRewardAmount());
        }

        Quest oneColumnLess = questsModel.getQuestByName("One Column Less");
        int targetColumn = oneColumnLess.getTargetValue()[0];
        if (isColumnEmpty(targetColumn)) {
            completeQuest(oneColumnLess, oneColumnLess.getRewardAmount());
        }

        Quest defenselessRow = questsModel.getQuestByName("Defenseless Row");
        int targetRow = defenselessRow.getTargetValue()[0];
        if (isRowEmpty(targetRow)) {
            completeQuest(defenselessRow, defenselessRow.getRewardAmount());
        }

        Quest defenselessCross = questsModel.getQuestByName("Defenseless Cross");
        int targetCross = defenselessCross.getTargetValue()[0];
        if (isColumnEmpty(targetCross) && isRowEmpty(targetCross)) {
            completeQuest(defenselessCross, defenselessCross.getRewardAmount());
        }
    }

    private static boolean isColumnEmpty(int column) {
        for (Plant plant : GameManagerController.getInstance().getCurrentLevel().getActivePlants()) {
            if (plant.getX() == column) {
                return false;
            }
        }
        return true;
    }

    private static boolean isRowEmpty(int row) {
        for (Plant plant : GameManagerController.getInstance().getCurrentLevel().getActivePlants()) {
            if (plant.getY() == row) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasNoMirroredPlantPairs() {
        int rows = GameManagerController.getInstance().getCurrentLevel().getGameMap().getRows();
        int columns = GameManagerController.getInstance().getCurrentLevel().getGameMap().getColumns();

        for (int column = 1; column <= columns; column++) {
            for (int row = 1; row <= rows / 2; row++) {
                String firstPlant = getPlantNameAt(column, row);
                String mirroredPlant = getPlantNameAt(column, rows + 1 - row);
                if (Objects.equals(firstPlant, mirroredPlant)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static String getPlantNameAt(int column, int row) {
        for (Plant plant : GameManagerController.getInstance().getCurrentLevel().getActivePlants()) {
            if (plant.getX() == column && plant.getY() == row) {
                return plant.getData().getName();
            }
        }
        return null;
    }

    public static void notifyZombiesKilledByLawnmower(int killedCount) {
        if (killedCount <= 0) {
            return;
        }
        Quest mowingTime = questsModel.getQuestByName("Mowing Time");
        if (mowingTime == null || mowingTime.isCompleted()) {
            return;
        }

        mowingTime.setCurrentValue(mowingTime.getCurrentValue() + killedCount);
        int target = mowingTime.getTargetValue()[0];
        if (mowingTime.getCurrentValue() >= target) {
            completeQuest(mowingTime, target);
            mowingTime.setCompleted(true);
        }
    }

    public static void onZombieDefeated(Plant killerPlant){
        if (killerPlant == null) return;
        Quest proPlantPlayer = questsModel.getQuestByName("Pro Plant Player");
        if (killerPlant.getData().getName().equalsIgnoreCase(proPlantPlayer.getTargetPlant().getName())) {
            proPlantPlayer.setCurrentValue(proPlantPlayer.getCurrentValue() + 1);
        }

         isItDone(proPlantPlayer);

        Quest onlyCactus = questsModel.getQuestByName("Only Cactus");
        if (killerPlant.getData().getName().equalsIgnoreCase("cactus")) {
            onlyCactus.setCurrentValue(onlyCactus.getCurrentValue() + 1);
        }

        isItDone(onlyCactus);

        Quest familySlaughter = questsModel.getQuestByName("Family Slaughter");
        if (killerPlant.getData().getCategory() != familySlaughter.getTargetPlant().getCategory()){
            brokeFamilyRule = true;
        }
    }

    public static void isItDone(Quest quest) {
        for (int i = 0; i < quest.getTargetValue().length; i++) {
            if (quest.getCurrentValue() >= quest.getTargetValue()[i]) {
                completeQuest(quest, quest.getRewardAmount());
                return;
            }
        }
    }
}
