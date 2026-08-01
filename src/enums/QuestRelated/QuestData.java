package enums.QuestRelated;

import static enums.QuestRelated.Priority.*;
import static enums.QuestRelated.QuestCategory.DAILY;
import static enums.QuestRelated.QuestCategory.MAIN;
import static enums.QuestRelated.QuestCategory.CHALLENGE;
import static enums.QuestRelated.QuestObjective.*;
import static enums.QuestRelated.RewardType.*;

public enum QuestData {
    DAILY_SUN_COLLECTOR("Daily Sun Collector", DAILY,
            "Collect sun_amount units of sun in a single day", COIN, -1,
            BLUE, MEDIUM, new int[]{3000, 4000, 5000}, false),
    CHAPTER_HUNTER("Chapter Hunter", MAIN,
            "Defeat 50 zombies from the chapter season", SEED_PACKET, 10,
            ORANGE, HIGH, new int[]{50}, false),
    PRO_PLANT_PLAYER("Pro Plant Player", DAILY,
            "Kill 10 zombies only with Plant", UNLOCKABLE, -1,
            ORANGE, HIGH, new int[]{10}, true),
    ONLY_CACTUS("Only Cactus", DAILY,
            "Kill 10 zombies only with cactus", GEM, 20,
            ORANGE, HIGH, new int[]{10}, true),
    ECONOMIC_HERBIVORE("Economic Herbivore", MAIN,
            "Win a level without losing more than n plants", SEED_PACKET, -1,
            ORANGE, HIGH, new int[]{0, 1, 2, 3, 4, 5}, false),
    DEFENSE_MASTER("Defense Master", CHALLENGE,
            "Finish a level with exactly zero sun", GEM, 200,
            GREEN, CRITICAL, new int[]{1}, false),
    SPEED_EXECUTION("Speed Execution", MAIN,
            "Kill 10 zombies in less than 30 seconds from the start of the first wave", COIN, 500,
            BLUE, MEDIUM, new int[]{10}, false),
    PROFESSIONAL_DEMOLISHER("Professional Demolisher", DAILY,
            "Use 3 explosive plants in a single level", COIN, 100,
            BLACK, LOW, new int[]{3}, false),
    SYMMETRY("Symmetry", DAILY,
            "Win a level with a symmetrical garden", COIN, 500,
            ORANGE, HIGH, new int[]{1}, false),
    FAMILY_SLAUGHTER("Family Slaughter", DAILY,
            "Use only plants of family_type to kill zombies in a level", COIN, 1000,
            BLUE, MEDIUM, new int[]{1}, true),
    BLOOMING_IN_LIMITS("Blooming in Limits", DAILY,
            "Win a level without using plants from family_type", GEM, 100,
            ORANGE, HIGH, new int[]{1}, true),
    NIGHT_OR_MORNING("Night or Morning", CHALLENGE,
            "Complete a day level using only night plants and mushrooms", GEM, 20,
            ORANGE, HIGH, new int[]{1}, false),
    WIN_STREAK("Win Streak", DAILY,
            "Win 5 levels in a row on the highest difficulty", COIN, 5000,
            ORANGE, MEDIUM, new int[]{5}, false),
    ALMOST_VICTORIOUS("Almost Victorious", DAILY,
            "Kill 10 zombies in the first column of a row without a lawnmower", COIN, 300,
            BLUE, MEDIUM, new int[]{10}, false),
    NO_OCD("No OCD", DAILY,
            "Win a level with no mirrored plant pairs except the middle row", COIN, 800,
            BLUE, MEDIUM, new int[]{1}, false),
    CLOUDY_DAY("Cloudy Day", DAILY,
            "Win a level using only 3 sun-producing plants", GEM, 10,
            BLUE, HIGH, new int[]{3}, false),
    ONE_COLUMN_LESS("One Column Less", DAILY,
            "Win a level without planting in column n", GEM, 10,
            ORANGE, HIGH, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, false),
    DEFENSELESS_ROW("Defenseless Row", DAILY,
            "Win a level without planting in row n", GEM, 20,
            ORANGE, HIGH, new int[]{1, 2, 3, 4, 5}, false),
    DEFENSELESS_CROSS("Defenseless Cross", DAILY,
            "Win a level without planting in row n or column n", GEM, 25,
            ORANGE, HIGH, new int[]{1, 2, 3, 4, 5}, false),
    MOWING_TIME("Mowing Time", CHALLENGE,
            "Kill at least n zombies using lawnmowers", GEM, -1,
            ORANGE, MEDIUM, new int[]{10, 20, 30, 40, 50}, false);

    private final String questName;
    private final QuestCategory category;
    private final String conditionText;
    private final RewardType reward;
    private final int rewardAmount;
    private final QuestObjective objective;
    private final Priority priority;
    private final int[] targetValue;
    private final boolean needsPlant;

    QuestData(String questName, QuestCategory category, String conditionText, RewardType reward, int rewardAmount,
              QuestObjective objective, Priority priority, int[] targetValue, boolean needsPlant) {
        this.questName = questName;
        this.category = category;
        this.conditionText = conditionText;
        this.reward = reward;
        this.rewardAmount = rewardAmount;
        this.objective = objective;
        this.priority = priority;
        this.targetValue = targetValue;
        this.needsPlant = needsPlant;
    }

    public String getQuestName() {
        return questName;
    }

    public QuestCategory getCategory() {
        return category;
    }

    public String getConditionText() {
        return conditionText;
    }

    public RewardType getReward() {
        return reward;
    }

    public QuestObjective getObjective() {
        return objective;
    }

    public Priority getPriority() {
        return priority;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public int[] getTargetValue() {
        return targetValue;
    }

    public boolean isNeedsPlant() {
        return needsPlant;
    }
}
