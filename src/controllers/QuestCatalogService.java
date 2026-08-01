package controllers;

import controllers.menus.SignupMenuController;
import enums.PlantCategory;
import enums.QuestRelated.QuestCategory;
import enums.QuestRelated.QuestData;
import enums.QuestRelated.RewardType;
import models.App;
import models.LevelData;
import models.Quest;
import models.QuestsModel;
import models.User;
import models.plants.PlantData;
import models.plants.PlantRepository;
import models.seasons.Season;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class QuestCatalogService {
    private static final Random RANDOM = new Random();
    private static final String[] DEFAULT_SEASONS = {
            "Ancient Egypt", "Frostbite Caves", "Big Wave Beach", "Dark Ages"
    };

    private QuestCatalogService() {
    }

    static User user() {
        return App.getCurrentUser();
    }

    static QuestsModel model() {
        User user = user();
        return user == null ? null : user.getQuestsModel();
    }

    static void initialize() {
        generateAllQuests();
        refreshDailyQuests();
    }

    static void generateAllQuests() {
        User user = user();
        if (user == null) {
            return;
        }
        QuestsModel model = user.getQuestsModel();
        model.getAvailableQuests();
        boolean changed = false;
        for (QuestData data : QuestData.values()) {
            Quest quest = model.getQuestByData(data);
            if (quest == null) {
                quest = new Quest(data);
                configureQuest(quest, true);
                model.addQuest(quest);
                changed = true;
            } else {
                quest.ensureInitialized();
                configureQuest(quest, false);
            }
        }
        if (changed) {
            SignupMenuController.saveToJson();
        }
    }

    private static void configureQuest(Quest quest, boolean forceRandomValues) {
        if (quest == null || quest.getQuestData() == null) {
            return;
        }
        QuestData data = quest.getQuestData();
        if (forceRandomValues || quest.getTargetValue() == null || quest.getTargetValue().length == 0) {
            setQuestTarget(quest);
        }
        if (data == QuestData.CHAPTER_HUNTER
                && (forceRandomValues || quest.getTargetSeasonName() == null || quest.getTargetSeasonName().isBlank())) {
            quest.setTargetSeasonName(selectTargetSeasonName());
        }
        if (data.isNeedsPlant() && (forceRandomValues || quest.getTargetPlant() == null)) {
            quest.setTargetPlant(selectTargetPlant(data));
        }
        if (usesSeedPacketReward(data) && (forceRandomValues || quest.getRewardPlant() == null)) {
            quest.setRewardPlant(randomUnlockedOrAnyPlant());
        }
    }

    private static boolean usesSeedPacketReward(QuestData data) {
        return data == QuestData.CHAPTER_HUNTER || data == QuestData.ECONOMIC_HERBIVORE;
    }

    private static void setQuestTarget(Quest quest) {
        QuestData data = quest.getQuestData();
        int[] possible = data.getTargetValue();
        if (possible == null || possible.length == 0) {
            quest.setTargetValue(new int[]{1});
            return;
        }
        switch (data) {
            case DAILY_SUN_COLLECTOR, ECONOMIC_HERBIVORE, MOWING_TIME ->
                    quest.setTargetValue(new int[]{possible[RANDOM.nextInt(possible.length)]});
            case ONE_COLUMN_LESS -> quest.setTargetValue(new int[]{1 + RANDOM.nextInt(9)});
            case DEFENSELESS_ROW, DEFENSELESS_CROSS -> quest.setTargetValue(new int[]{1 + RANDOM.nextInt(5)});
            default -> quest.setTargetValue(new int[]{possible[0]});
        }
    }

    private static String selectTargetSeasonName() {
        List<Season> seasons = accessibleSeasons();
        if (seasons.isEmpty()) {
            return DEFAULT_SEASONS[0];
        }
        return seasons.get(RANDOM.nextInt(seasons.size())).getName();
    }

    private static List<Season> accessibleSeasons() {
        List<Season> allSeasons = App.getAllSeasons();
        List<Season> result = new ArrayList<>();
        if (allSeasons == null || allSeasons.isEmpty()) {
            return result;
        }
        User user = user();
        int lastAccessibleIndex = 0;
        if (user != null && user.getLastSeasonId() > 0) {
            for (int i = 0; i < allSeasons.size(); i++) {
                Season season = allSeasons.get(i);
                if (season != null && season.getData() != null
                        && season.getData().getId() == user.getLastSeasonId()) {
                    lastAccessibleIndex = i;
                    LevelData lastLevel = user.getLastLevel();
                    if (lastLevel != null && season.getLevels() != null
                            && lastLevel.getLevelNumber() >= season.getLevels().size()
                            && i + 1 < allSeasons.size()) {
                        lastAccessibleIndex = i + 1;
                    }
                    break;
                }
            }
        }
        for (int i = 0; i <= lastAccessibleIndex && i < allSeasons.size(); i++) {
            Season season = allSeasons.get(i);
            if (season != null && season.getName() != null && !season.getName().isBlank()) {
                result.add(season);
            }
        }
        return result;
    }

    private static PlantData selectTargetPlant(QuestData data) {
        return switch (data) {
            case PRO_PLANT_PLAYER -> randomKillerPlant();
            case ONLY_CACTUS -> PlantRepository.getInstance().findByName("Cactus");
            case FAMILY_SLAUGHTER, BLOOMING_IN_LIMITS -> randomFamilyPlant();
            default -> null;
        };
    }

    private static List<PlantData> unlockedPlantData() {
        List<PlantData> plants = new ArrayList<>();
        User user = user();
        if (user == null) {
            return plants;
        }
        for (String id : user.getCollection().getAvailablePlantsIds()) {
            PlantData plant = PlantRepository.getInstance().findById(id);
            if (plant != null) {
                plants.add(plant);
            }
        }
        return plants;
    }

    private static List<PlantData> eligiblePlants() {
        List<PlantData> candidates = unlockedPlantData();
        if (!candidates.isEmpty()) {
            return candidates;
        }
        List<Season> seasons = accessibleSeasons();
        if (seasons.isEmpty() || seasons.get(0).getLevels() == null || seasons.get(0).getLevels().isEmpty()) {
            return candidates;
        }
        User user = user();
        for (String plantName : seasons.get(0).getLevels().get(0).getAvailablePlants()) {
            PlantData plant = PlantRepository.getInstance().findById(plantName);
            if (plant == null) {
                plant = PlantRepository.getInstance().findByName(plantName);
            }
            if (plant != null && !candidates.contains(plant)) {
                candidates.add(plant);
                if (user != null) {
                    user.getCollection().unlockPlant(plant.getId());
                }
            }
        }
        return candidates;
    }

    private static PlantData randomUnlockedOrAnyPlant() {
        List<PlantData> candidates = eligiblePlants();
        return candidates.isEmpty() ? null : candidates.get(RANDOM.nextInt(candidates.size()));
    }

    private static PlantData randomFamilyPlant() {
        List<PlantData> candidates = new ArrayList<>();
        for (PlantData plant : eligiblePlants()) {
            if (plant != null && plant.getCategory() != null) {
                candidates.add(plant);
            }
        }
        return candidates.isEmpty() ? null : candidates.get(RANDOM.nextInt(candidates.size()));
    }

    private static PlantData randomKillerPlant() {
        List<PlantData> killers = new ArrayList<>();
        for (PlantData plant : eligiblePlants()) {
            if (plant == null || plant.getCategory() == null) {
                continue;
            }
            PlantCategory category = plant.getCategory();
            if (category == PlantCategory.SHOOTER || category == PlantCategory.LOBBER
                    || category == PlantCategory.EXPLOSIVE || category == PlantCategory.MELEE
                    || category == PlantCategory.STRIKE_TROUGH || category == PlantCategory.HOMING) {
                killers.add(plant);
            }
        }
        return killers.isEmpty() ? null : killers.get(RANDOM.nextInt(killers.size()));
    }

    static PlantData getRandomPlant(String type) {
        if (type == null) {
            return null;
        }
        if (type.equalsIgnoreCase("killer")) {
            return randomKillerPlant();
        }
        if (type.equalsIgnoreCase("cactus")) {
            return PlantRepository.getInstance().findByName("Cactus");
        }
        return randomUnlockedOrAnyPlant();
    }

    static PlantData getRandomPlant() {
        return randomUnlockedOrAnyPlant();
    }

    static void refreshDailyQuests() {
        User user = user();
        QuestsModel model = model();
        if (user == null || model == null) {
            return;
        }
        String today = LocalDate.now().toString();
        if (today.equals(user.getLastDailyQuestRefreshDate())) {
            return;
        }
        for (Quest quest : model.getAvailableQuests()) {
            if (quest.getCategory() != QuestCategory.DAILY) {
                continue;
            }
            quest.reset();
            setQuestTarget(quest);
            quest.setRewardPlant(null);
            quest.setTargetPlant(quest.getQuestData().isNeedsPlant()
                    ? selectTargetPlant(quest.getQuestData()) : null);
        }
        user.setQuestWinStreak(0);
        user.setLastDailyQuestRefreshDate(today);
        QuestController.onLevelStarted();
        SignupMenuController.saveToJson();
    }

    static boolean isReady() {
        QuestsModel model = model();
        return model != null && model.getAvailableQuests().size() == QuestData.values().length;
    }

    static boolean prepare() {
        if (user() == null) {
            return false;
        }
        generateAllQuests();
        refreshDailyQuests();
        return isReady();
    }

    static String claimReward(Quest quest, int amount) {
        User user = user();
        if (quest == null || quest.isCompleted() || user == null) {
            return "reward is not available";
        }
        int safeAmount = Math.max(0, amount);
        String rewardText = applyReward(user, quest, safeAmount);
        quest.setCompleted(true);
        user.recordQuestCompletion(quest.getCategory());
        SignupMenuController.saveToJson();
        return quest.getQuestName() + " completed: " + rewardText;
    }

    private static String applyReward(User user, Quest quest, int amount) {
        RewardType rewardType = quest.getReward();
        if (rewardType == null) {
            return "no reward";
        }
        return switch (rewardType) {
            case COIN -> {
                user.addCoins(amount);
                yield amount + " coins";
            }
            case GEM -> {
                user.addGems(amount);
                yield amount + " gems";
            }
            case UNLOCKABLE -> unlockPlantReward(user, quest);
            case SEED_PACKET -> seedPacketReward(user, quest, amount);
        };
    }

    private static String unlockPlantReward(User user, Quest quest) {
        PlantData plant = getRewardPlant();
        if (plant == null) {
            return "all plants are already unlocked";
        }
        quest.setRewardPlant(plant);
        user.getCollection().unlockPlant(plant.getId());
        return displayName(plant) + " unlocked";
    }

    private static String seedPacketReward(User user, Quest quest, int amount) {
        PlantData plant = quest.getRewardPlant();
        if (plant == null) {
            plant = quest.getTargetPlant();
        }
        if (plant == null) {
            plant = randomUnlockedOrAnyPlant();
        }
        if (plant == null) {
            return "no plant is available for seed packets";
        }
        quest.setRewardPlant(plant);
        user.addSeedPackets(plant.getName(), amount);
        return amount + " seed packets for " + displayName(plant);
    }

    private static String displayName(PlantData plant) {
        return plant.getDisplayName() == null || plant.getDisplayName().isBlank()
                ? plant.getName() : plant.getDisplayName();
    }

    static PlantData getRewardPlant() {
        User user = user();
        if (user == null) {
            return null;
        }
        List<PlantData> lockedPlants = App.getLockedPlants();
        if (lockedPlants == null || lockedPlants.isEmpty()) {
            return null;
        }
        return lockedPlants.get(RANDOM.nextInt(lockedPlants.size()));
    }

    static Quest quest(QuestData data) {
        QuestsModel model = model();
        return model == null ? null : model.getQuestByData(data);
    }

    static boolean active(Quest quest) {
        return quest != null && !quest.isCompleted();
    }

    static int rewardAmountFor(Quest quest) {
        if (quest == null || quest.getQuestData() == null) {
            return 0;
        }
        if (quest.getQuestData() == QuestData.DAILY_SUN_COLLECTOR) {
            return quest.getSelectedTarget() / 100;
        }
        if (quest.getQuestData() == QuestData.ECONOMIC_HERBIVORE) {
            return Math.max(0, 20 - quest.getSelectedTarget());
        }
        if (quest.getQuestData() == QuestData.MOWING_TIME) {
            return quest.getSelectedTarget();
        }
        return Math.max(0, quest.getRewardAmount());
    }

    static void completeAtTarget(Quest quest) {
        if (active(quest) && quest.getSelectedTarget() > 0
                && quest.getCurrentValue() >= quest.getSelectedTarget()) {
            System.out.println(claimReward(quest, rewardAmountFor(quest)));
        }
    }
}
