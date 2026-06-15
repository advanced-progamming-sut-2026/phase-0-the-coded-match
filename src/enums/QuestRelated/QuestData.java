package enums.QuestRelated;

import enums.RewardType;

import static enums.QuestRelated.Priority.*;
import static enums.QuestRelated.QuestCategory.DAILY;
import static enums.QuestRelated.QuestCategory.MAIN;
import static enums.QuestRelated.QuestCategory.CHALLENGE;
import static enums.QuestRelated.QuestObjective.*;
import static enums.RewardType.*;

public enum QuestData {
    DAILY_SUN_COLLECTOR("Daily Sun Collector", DAILY, "Collect sun_amount units of sun in a single day", COIN, BLUE, MEDIUM ),
    CHAPTER_HUNTER("Chapter Hunter", MAIN, "Defeat 50 zombies from the chapter season", SEED_PACKET, ORANGE, HIGH),
    PRO_PLANT_PLAYER("Pro Plant Player", DAILY, "Kill 10 zombies only with Plant", UNLOCKABLE, ORANGE, HIGH),
    ONLY_CACTUS("Only Cactus", DAILY, "Kill 10 zombies only with cactus", GEM, ORANGE, HIGH),
    ECONOMIC_HERBIVORE("Economic Herbivore", MAIN, "Victory in a stage without losing more than n plants", SEED_PACKET, ORANGE, HIGH),
    DEFENSE_MASTER("Defense Master", CHALLENGE, "Finish a stage with exactly zero sun", GEM, GREEN, CRITICAL),
    SPEED_EXECUTION("Speed Execution", MAIN, "Kill 10 zombies in less than 30 seconds from the start of the first wave of the zombie attack", COIN, BLUE, MEDIUM),
    PROFESSIONAL_DEMOLISHER("Professional Demolisher", DAILY, "Use 3 explosive plants in a single stage", COIN, BLACK, LOW),
    SYMMETRY("Symmetry", DAILY, "The game garden layout must ultimately be symmetrical", COIN, ORANGE, HIGH),
    FAMILY_SLAUGHTER("Family Slaughter", DAILY, "Only use plants of family_type to kill zombies", COIN, BLUE, MEDIUM),
    BLOOMING_IN_LIMITS("Blooming in Limits", DAILY, "To win the stage, no plants from the family_type family should be used", GEM, ORANGE, HIGH),
    NIGHT_OR_MORNING("Night or Morning", CHALLENGE, "Completing a Day game using only Night plants (mushrooms)", GEM, ORANGE, HIGH),
    WIN_STREAK("Win Streak", DAILY, "Win 5 stages in a row on the highest difficulty", COIN, ORANGE, MEDIUM),
    ALMOST_VICTORIOUS("Almost Victorious", DAILY, "Kill 10 zombies in the first column of a row that does not have a lawnmower", COIN, BLUE, MEDIUM),
    NO_OCD("No OCD", DAILY, "Win the stage in a state where there is absolutely no symmetry in the garden (except for the middle row)", COIN, BLUE, MEDIUM),
    CLOUDY_DAY("Cloudy Day", DAILY, "Beat a stage using only 3 sun-producing plants", GEM, BLUE, HIGH),
    ONE_COLUMN_LESS("One Column Less", DAILY, "Win a stage provided that no plants are planted in the n-th column", GEM, ORANGE, HIGH),
    DEFENSELESS_ROW("Defenseless Row", DAILY, "Win a stage provided that no plants are planted in the $n$-th row", GEM, ORANGE, HIGH),
    DEFENSELESS_CROSS("Defenseless Cross", DAILY, "Win a stage provided that both the $n$-th column and $n$-th row are completely empty", GEM, ORANGE, HIGH),
    MOWING_TIME("Mowing Time", CHALLENGE, "Kill at least n zombies using the lawnmowers", GEM, ORANGE, MEDIUM);


    private final String questName;
    private final QuestCategory category;
    private final String conditionText;
    private final RewardType reward;
    private final QuestObjective objective;
    private final Priority priority;

     QuestData(String questName, QuestCategory category, String conditionText, RewardType reward, QuestObjective objective, Priority priority){
        this.questName=questName;
        this.category=category;
        this.conditionText=conditionText;
        this.reward=reward;
        this.objective=objective;
        this.priority=priority;
    }

}
