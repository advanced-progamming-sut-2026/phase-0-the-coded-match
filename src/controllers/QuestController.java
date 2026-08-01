package controllers;

import enums.PlantCategory;
import enums.PlantTag;
import enums.QuestRelated.QuestData;
import models.App;
import models.Level;
import models.Quest;
import models.QuestRuntimeState;
import models.QuestsModel;
import models.User;
import models.plants.Plant;
import models.plants.PlantData;
import models.seasons.Season;
import models.zombies.Zombie;

import java.util.List;
import java.util.Locale;

public class QuestController {
    private static final int TICKS_PER_SECOND = 10;

    private QuestController() {
    }

    private static User user() {
        return QuestCatalogService.user();
    }

    private static QuestsModel model() {
        return QuestCatalogService.model();
    }

    private static QuestRuntimeState state() {
        User user = user();
        return user == null ? null : user.getQuestRuntimeState();
    }

    private static Quest quest(QuestData data) {
        return QuestCatalogService.quest(data);
    }

    private static boolean active(Quest quest) {
        return QuestCatalogService.active(quest);
    }

    private static int rewardAmountFor(Quest quest) {
        return QuestCatalogService.rewardAmountFor(quest);
    }

    private static void completeAtTarget(Quest quest) {
        QuestCatalogService.completeAtTarget(quest);
    }

    public static void initializeForCurrentUser() {
        QuestCatalogService.initialize();
    }

    public static void generateAllQuests() {
        QuestCatalogService.generateAllQuests();
    }

    public static PlantData getRandomPlant(String type) {
        return QuestCatalogService.getRandomPlant(type);
    }

    public static PlantData getRandomPlant() {
        return QuestCatalogService.getRandomPlant();
    }

    public static void refreshDailyQuests() {
        QuestCatalogService.refreshDailyQuests();
    }

    public static boolean isReady() {
        return QuestCatalogService.isReady();
    }

    public static String claimReward(Quest quest, int amount) {
        return QuestCatalogService.claimReward(quest, amount);
    }

    public static PlantData getRewardPlant() {
        return QuestCatalogService.getRewardPlant();
    }

    public static void onLevelStarted() {
        QuestRuntimeState runtimeState = state();
        if (runtimeState != null) {
            runtimeState.resetLevelState();
        }
        QuestsModel model = model();
        if (model == null) {
            return;
        }
        QuestData[] levelQuests = {
                QuestData.PRO_PLANT_PLAYER,
                QuestData.ONLY_CACTUS,
                QuestData.DEFENSE_MASTER,
                QuestData.SPEED_EXECUTION,
                QuestData.PROFESSIONAL_DEMOLISHER,
                QuestData.ECONOMIC_HERBIVORE,
                QuestData.SYMMETRY,
                QuestData.FAMILY_SLAUGHTER,
                QuestData.BLOOMING_IN_LIMITS,
                QuestData.NIGHT_OR_MORNING,
                QuestData.NO_OCD,
                QuestData.CLOUDY_DAY,
                QuestData.ONE_COLUMN_LESS,
                QuestData.DEFENSELESS_ROW,
                QuestData.DEFENSELESS_CROSS
        };
        for (QuestData data : levelQuests) {
            resetLevelQuestProgress(model.getQuestByData(data));
        }
    }

    private static void resetLevelQuestProgress(Quest quest) {
        if (quest != null && !quest.isCompleted()) {
            quest.setCurrentValue(0);
        }
    }

    public static void notifyWaveStarted(int waveNumber, double startTick) {
        if (!QuestCatalogService.prepare() || waveNumber != 1) {
            return;
        }
        QuestRuntimeState runtimeState = state();
        Quest speed = quest(QuestData.SPEED_EXECUTION);
        if (runtimeState != null) {
            runtimeState.setFirstWaveStartTick(startTick);
        }
        if (active(speed)) {
            speed.setCurrentValue(0);
        }
    }

    public static void notifySunCollected(int value) {
        if (!QuestCatalogService.prepare() || value <= 0) {
            return;
        }
        Quest daily = quest(QuestData.DAILY_SUN_COLLECTOR);
        if (!active(daily)) {
            return;
        }
        daily.addProgress(value);
        completeAtTarget(daily);
    }

    public static void notifyZombieKilled(Season season) {
        if (!QuestCatalogService.prepare() || season == null) {
            return;
        }
        Quest hunter = quest(QuestData.CHAPTER_HUNTER);
        if (!active(hunter) || hunter.getTargetSeasonName() == null) {
            return;
        }
        if (normalize(hunter.getTargetSeasonName()).equals(normalize(season.getName()))) {
            hunter.addProgress(1);
            completeAtTarget(hunter);
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private static void updateSpeedExecution() {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        Quest speed = quest(QuestData.SPEED_EXECUTION);
        QuestRuntimeState runtimeState = state();
        if (!active(speed) || runtimeState == null || level == null || runtimeState.getFirstWaveStartTick() < 0) {
            return;
        }
        double elapsedTicks = level.getCurrentTick() - runtimeState.getFirstWaveStartTick();
        if (elapsedTicks >= 0 && elapsedTicks <= 30.0 * TICKS_PER_SECOND) {
            speed.addProgress(1);
            completeAtTarget(speed);
        }
    }

    public static void notifyZombieKilled(Zombie zombie) {
        if (!QuestCatalogService.prepare() || zombie == null) {
            return;
        }
        updateSpeedExecution();
        Quest almost = quest(QuestData.ALMOST_VICTORIOUS);
        if (!active(almost)) {
            return;
        }
        int row = zombie.getY();
        boolean inFirstColumn = zombie.getX() > 0 && zombie.getX() <= 1.25;
        if (inFirstColumn && App.lawnMowerUsed(row) == null) {
            almost.addProgress(1);
            completeAtTarget(almost);
        }
    }

    public static void notifyNoSunsLeft() {
        if (!QuestCatalogService.prepare()) {
            return;
        }
        completeBooleanQuest(QuestData.DEFENSE_MASTER, true);
    }

    public static void notifyPlantsDestroyed(int count) {
        if (!QuestCatalogService.prepare()) {
            return;
        }
        Quest economic = quest(QuestData.ECONOMIC_HERBIVORE);
        if (!active(economic)) {
            return;
        }
        int lost = Math.max(0, count);
        economic.setCurrentValue(lost);
        if (lost <= economic.getSelectedTarget()) {
            System.out.println(claimReward(economic, rewardAmountFor(economic)));
        }
    }

    public static void onPlantPlaced(Plant plant) {
        if (!QuestCatalogService.prepare() || plant == null || plant.getData() == null) {
            return;
        }
        QuestRuntimeState runtimeState = state();
        if (runtimeState == null) {
            return;
        }
        runtimeState.getPlantedColumnsThisLevel().add(plant.getX());
        runtimeState.getPlantedRowsThisLevel().add(plant.getY());
        updateDemolisher(plant, runtimeState);
        updateBloomingInLimits(plant, runtimeState);
        updateNightOrMorning(plant, runtimeState);
        updateCloudyDay(plant, runtimeState);
    }

    private static void updateDemolisher(Plant plant, QuestRuntimeState runtimeState) {
        Quest demolisher = quest(QuestData.PROFESSIONAL_DEMOLISHER);
        if (active(demolisher) && plant.hasThisTag(PlantTag.EXPLOSIVE)) {
            demolisher.setCurrentValue(runtimeState.incrementExplosivePlantsPlacedThisLevel());
            completeAtTarget(demolisher);
        }
    }

    private static void updateBloomingInLimits(Plant plant, QuestRuntimeState runtimeState) {
        Quest blooming = quest(QuestData.BLOOMING_IN_LIMITS);
        if (active(blooming) && blooming.getTargetPlant() != null
                && plant.getData().getCategory() == blooming.getTargetPlant().getCategory()) {
            runtimeState.setUsedForbiddenFamily(true);
        }
    }

    private static void updateNightOrMorning(Plant plant, QuestRuntimeState runtimeState) {
        Quest nightQuest = quest(QuestData.NIGHT_OR_MORNING);
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (!active(nightQuest) || level == null || level.getData() == null || !level.getData().isDay()) {
            return;
        }
        boolean nightPlant = plant.hasThisTag(PlantTag.NIGHT) || plant.hasThisTag(PlantTag.SHROOM);
        if (nightPlant) {
            runtimeState.setNightPlantUsed(true);
        } else {
            runtimeState.setNightOnlyRuleBroken(true);
        }
    }

    private static void updateCloudyDay(Plant plant, QuestRuntimeState runtimeState) {
        Quest cloudy = quest(QuestData.CLOUDY_DAY);
        if (!active(cloudy)) {
            return;
        }
        if (isSunProducingPlant(plant)) {
            runtimeState.incrementSunProducerPlantsPlacedThisLevel();
        } else {
            runtimeState.setCloudyDayRuleBroken(true);
        }
    }

    private static boolean isSunProducingPlant(Plant plant) {
        if (plant.getData().getCategory() == PlantCategory.SUN_PRODUCER || plant.hasThisTag(PlantTag.SUN)) {
            return true;
        }
        List<String> abilities = plant.getData().getAbilities();
        return abilities != null && abilities.stream().anyMatch(value -> value.equalsIgnoreCase("PRODUCE_SUN"));
    }

    public static void onLevelCompleted(boolean levelWon) {
        if (!QuestCatalogService.prepare()) {
            onLevelStarted();
            return;
        }
        User currentUser = user();
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (!levelWon || level == null) {
            currentUser.setQuestWinStreak(0);
            Quest winStreak = quest(QuestData.WIN_STREAK);
            if (active(winStreak)) {
                winStreak.setCurrentValue(0);
            }
            onLevelStarted();
            return;
        }
        completeMapAndRestrictionQuests(level);
        updateWinStreak(level);
        onLevelStarted();
    }

    private static void completeMapAndRestrictionQuests(Level level) {
        QuestRuntimeState runtimeState = state();
        if (runtimeState == null || level.getGameMap() == null) {
            return;
        }
        completeBooleanQuest(QuestData.SYMMETRY, QuestMapEvaluator.isGardenSymmetric(level));
        completeFamilySlaughter(runtimeState);
        Quest blooming = quest(QuestData.BLOOMING_IN_LIMITS);
        completeBooleanQuest(blooming, blooming != null && blooming.getTargetPlant() != null
                && !runtimeState.isUsedForbiddenFamily());
        completeNightOrMorning(level, runtimeState);
        completeBooleanQuest(QuestData.NO_OCD, QuestMapEvaluator.hasNoMirroredPlantPairs(level));
        completeCloudyDay(runtimeState);
        completeEmptyLineQuests(runtimeState);
    }

    private static void completeFamilySlaughter(QuestRuntimeState runtimeState) {
        Quest family = quest(QuestData.FAMILY_SLAUGHTER);
        if (active(family) && family.getTargetPlant() != null && !runtimeState.isBrokeFamilyRule()
                && runtimeState.getFamilyTargetKills() > 0) {
            family.setCurrentValue(runtimeState.getFamilyTargetKills());
            System.out.println(claimReward(family, rewardAmountFor(family)));
        }
    }

    private static void completeNightOrMorning(Level level, QuestRuntimeState runtimeState) {
        boolean completed = level.getData() != null && level.getData().isDay() && runtimeState.isNightPlantUsed()
                && !runtimeState.isNightOnlyRuleBroken();
        completeBooleanQuest(QuestData.NIGHT_OR_MORNING, completed);
    }

    private static void completeCloudyDay(QuestRuntimeState runtimeState) {
        Quest cloudy = quest(QuestData.CLOUDY_DAY);
        if (active(cloudy) && !runtimeState.isCloudyDayRuleBroken()
                && runtimeState.getSunProducerPlantsPlacedThisLevel() == cloudy.getSelectedTarget()) {
            cloudy.setCurrentValue(runtimeState.getSunProducerPlantsPlacedThisLevel());
            System.out.println(claimReward(cloudy, rewardAmountFor(cloudy)));
        }
    }

    private static void completeEmptyLineQuests(QuestRuntimeState runtimeState) {
        Quest oneColumn = quest(QuestData.ONE_COLUMN_LESS);
        boolean emptyColumn = active(oneColumn)
                && !runtimeState.getPlantedColumnsThisLevel().contains(oneColumn.getSelectedTarget());
        completeBooleanQuest(oneColumn, emptyColumn);

        Quest row = quest(QuestData.DEFENSELESS_ROW);
        boolean emptyRow = active(row)
                && !runtimeState.getPlantedRowsThisLevel().contains(row.getSelectedTarget());
        completeBooleanQuest(row, emptyRow);

        Quest cross = quest(QuestData.DEFENSELESS_CROSS);
        boolean emptyCross = active(cross)
                && !runtimeState.getPlantedRowsThisLevel().contains(cross.getSelectedTarget())
                && !runtimeState.getPlantedColumnsThisLevel().contains(cross.getSelectedTarget());
        completeBooleanQuest(cross, emptyCross);
    }

    private static void completeBooleanQuest(QuestData data, boolean condition) {
        completeBooleanQuest(quest(data), condition);
    }

    private static void completeBooleanQuest(Quest targetQuest, boolean condition) {
        if (active(targetQuest) && condition) {
            targetQuest.setCurrentValue(Math.max(1, targetQuest.getSelectedTarget()));
            System.out.println(claimReward(targetQuest, rewardAmountFor(targetQuest)));
        }
    }

    private static void updateWinStreak(Level level) {
        User currentUser = user();
        Quest win = quest(QuestData.WIN_STREAK);
        if (!active(win)) {
            return;
        }
        if (level.getLevelDifficulty() == 5) {
            currentUser.setQuestWinStreak(currentUser.getQuestWinStreak() + 1);
        } else {
            currentUser.setQuestWinStreak(0);
        }
        win.setCurrentValue(currentUser.getQuestWinStreak());
        completeAtTarget(win);
    }

    public static void notifyZombiesKilledByLawnmower(int killedCount) {
        if (!QuestCatalogService.prepare() || killedCount <= 0) {
            return;
        }
        notifyNonPlantZombieKilled(killedCount);
        Quest mowing = quest(QuestData.MOWING_TIME);
        if (!active(mowing)) {
            return;
        }
        mowing.addProgress(killedCount);
        completeAtTarget(mowing);
    }

    public static void notifyNonPlantZombieKilled(int killedCount) {
        if (!QuestCatalogService.prepare() || killedCount <= 0) {
            return;
        }
        resetExclusiveKillQuest(QuestData.PRO_PLANT_PLAYER);
        resetExclusiveKillQuest(QuestData.ONLY_CACTUS);
        QuestRuntimeState runtimeState = state();
        if (runtimeState != null) {
            runtimeState.setProPlantRuleBroken(true);
            runtimeState.setOnlyCactusRuleBroken(true);
            runtimeState.setBrokeFamilyRule(true);
        }
    }

    private static void resetExclusiveKillQuest(QuestData data) {
        Quest targetQuest = quest(data);
        if (active(targetQuest)) {
            targetQuest.setCurrentValue(0);
        }
    }

    public static void onZombieDefeated(Plant killerPlant) {
        if (!QuestCatalogService.prepare()) {
            return;
        }
        if (killerPlant == null || killerPlant.getData() == null) {
            notifyNonPlantZombieKilled(1);
            return;
        }
        updateProPlantPlayer(killerPlant);
        updateOnlyCactus(killerPlant);
        updateFamilySlaughter(killerPlant);
    }

    private static void updateProPlantPlayer(Plant killerPlant) {
        Quest pro = quest(QuestData.PRO_PLANT_PLAYER);
        QuestRuntimeState runtimeState = state();
        if (!active(pro) || pro.getTargetPlant() == null || runtimeState == null) {
            return;
        }
        if (!samePlant(killerPlant.getData(), pro.getTargetPlant())) {
            runtimeState.setProPlantRuleBroken(true);
            pro.setCurrentValue(0);
            return;
        }
        if (!runtimeState.isProPlantRuleBroken()) {
            pro.addProgress(1);
            completeAtTarget(pro);
        }
    }

    private static void updateOnlyCactus(Plant killerPlant) {
        Quest cactus = quest(QuestData.ONLY_CACTUS);
        QuestRuntimeState runtimeState = state();
        if (!active(cactus) || runtimeState == null) {
            return;
        }
        String plantName = killerPlant.getData().getName();
        if (plantName == null || !plantName.equalsIgnoreCase("Cactus")) {
            runtimeState.setOnlyCactusRuleBroken(true);
            cactus.setCurrentValue(0);
            return;
        }
        if (!runtimeState.isOnlyCactusRuleBroken()) {
            cactus.addProgress(1);
            completeAtTarget(cactus);
        }
    }

    private static void updateFamilySlaughter(Plant killerPlant) {
        Quest family = quest(QuestData.FAMILY_SLAUGHTER);
        QuestRuntimeState runtimeState = state();
        if (!active(family) || runtimeState == null || family.getTargetPlant() == null
                || killerPlant.getData().getCategory() == null) {
            return;
        }
        if (killerPlant.getData().getCategory() == family.getTargetPlant().getCategory()) {
            family.setCurrentValue(runtimeState.incrementFamilyTargetKills());
        } else {
            runtimeState.setBrokeFamilyRule(true);
        }
    }

    private static boolean samePlant(PlantData first, PlantData second) {
        if (first == null || second == null) {
            return false;
        }
        if (first.getId() != null && second.getId() != null) {
            return first.getId().equalsIgnoreCase(second.getId());
        }
        return first.getName() != null && first.getName().equalsIgnoreCase(second.getName());
    }

    public static void isItDone(Quest quest) {
        completeAtTarget(quest);
    }
}
