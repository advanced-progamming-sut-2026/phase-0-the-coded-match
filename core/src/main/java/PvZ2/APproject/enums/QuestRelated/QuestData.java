package PvZ2.APproject.enums.QuestRelated;


import static PvZ2.APproject.enums.QuestRelated.Priority.*;
import static PvZ2.APproject.enums.QuestRelated.QuestCategory.*;
import static PvZ2.APproject.enums.QuestRelated.QuestObjective.*;
import static PvZ2.APproject.enums.QuestRelated.RewardType.*;

public enum QuestData {
    DAILY_SUN_COLLECTOR("Daily Sun Collector", DAILY,
            "Collect sun_amount units of sun in a single day", COIN, -1,
            BLUE, MEDIUM, new int[]{3000, 4000, 5000}, false),
    ANCIENT_HUNTER("AncientEgypt Hunter", MAIN,
            "Defeat 50 zombies from the chapter season", SEED_PACKET, 10,
            ORANGE, HIGH, new int[]{50}, false),
    BIGWAVE_HUNTER("BigWaveBeach Hunter", MAIN,
            "Defeat 50 zombies from the chapter season", SEED_PACKET, 10,
            ORANGE, HIGH, new int[]{50}, false),
    DARKAGES_HUNTER("DarkAges Hunter", MAIN,
            "Defeat 50 zombies from the chapter season", SEED_PACKET, 10,
            ORANGE, HIGH, new int[]{50}, false),
    FROSTBITE_HUNTER("FrostbiteCaves Hunter", MAIN,
            "Defeat 50 zombies from the chapter season", SEED_PACKET, 10,
            ORANGE, HIGH, new int[]{50}, false),
    PRO_PLANT_PLAYER("Pro Plant Player", DAILY,
            "Kill 10 zombies only with Plant", UNLOCKABLE, -1,
            ORANGE, HIGH, new int[]{10}, true),
    ONLY_CACTUS("Only Cactus", DAILY,
            "Kill 10 zombies only with cactus", GEM, 20,
            ORANGE, HIGH, new int[]{10}, true),
    ECONOMIC_HERBIVORE("Economic Herbivore", MAIN,
            "Victory in a level without losing more than n plants", SEED_PACKET, -1,
            ORANGE, HIGH, new int[]{0, 1, 2, 3, 4, 5}, false),
    DEFENSE_MASTER("Defense Master", CHALLENGE,
            "Finish a level with exactly zero sun", GEM, 200,
            GREEN, CRITICAL, new int[]{0}, false),
    SPEED_EXECUTION("Speed Execution", MAIN,
            "Kill 10 zombies in less than 30 seconds from the start of the first wave of the zombie attack",
            COIN, 500, BLUE, MEDIUM, new int[]{10}, false),
    PROFESSIONAL_DEMOLISHER("Professional Demolisher", DAILY,
            "Use 3 explosive plants in a single stage", COIN, 100,
            BLACK, LOW, new int[]{3}, false),
    SYMMETRY("Symmetry", DAILY,
            "The game garden layout must ultimately be symmetrical", COIN,500,
            ORANGE, HIGH, new int[]{0}, false),
    FAMILY_SLAUGHTER("Family Slaughter", DAILY,
            "Only use plants of family_type to kill zombies", COIN, 1000,
            BLUE, MEDIUM, new int[]{0}, true),
    BLOOMING_IN_LIMITS("Blooming in Limits", DAILY,
            "To win the stage, no plants from the family_type family should be used", GEM, 100,
            ORANGE, HIGH, new int[]{0}, true),
    NIGHT_OR_MORNING("Night or Morning", CHALLENGE,
            "Completing a Day game using only Night plants (mushrooms)", GEM, 20,
            ORANGE, HIGH, new int[]{0}, false),
    WIN_STREAK("Win Streak", DAILY,
            "Win 5 stages in a row on the highest difficulty", COIN, 5000,
            ORANGE, MEDIUM, new int[]{0}, false),
    ALMOST_VICTORIOUS("Almost Victorious", DAILY,
            "Kill 10 zombies in the first column of a row that does not have a lawnmower", COIN, 300,
            BLUE, MEDIUM, new int[]{10}, false),
    NO_OCD("No OCD", DAILY,
            "Win the stage in a state where there is absolutely no symmetry in the garden (except for the middle row)",
            COIN, 800, BLUE, MEDIUM, new int[]{0}, false),
    CLOUDY_DAY("Cloudy Day", DAILY,
            "Beat a stage using only 3 sun-producing plants", GEM, 10,
            BLUE, HIGH, new int[]{3}, false),
    ONE_COLUMN_LESS("One Column Less", DAILY,
            "Win a stage provided that no plants are planted in the n-th column", GEM, 10,
            ORANGE, HIGH, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, false),
    DEFENSELESS_ROW("Defenseless Row", DAILY,
            "Win a stage provided that no plants are planted in the n-th row", GEM, 20,
            ORANGE, HIGH, new int[]{1, 2, 3, 4, 5}, false),
    DEFENSELESS_CROSS("Defenseless Cross", DAILY,
            "Win a stage provided that both the n-th column and n-th row are completely empty", GEM, 25,
            ORANGE, HIGH, new int[]{1, 2, 3, 4, 5}, false),
    MOWING_TIME("Mowing Time", CHALLENGE,
            "Kill at least n zombies using the lawnmowers", GEM, -1,
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
               QuestObjective objective, Priority priority, int[] targetValue, boolean needsPlant){
        this.questName=questName;
        this.category=category;
        this.conditionText=conditionText;
        this.reward=reward;
        this.rewardAmount = rewardAmount;
        this.objective=objective;
        this.priority=priority;
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
