package models;

import enums.QuestRelated.Priority;
import enums.QuestRelated.QuestCategory;
import enums.QuestRelated.QuestData;
import enums.QuestRelated.QuestObjective;
import enums.QuestRelated.RewardType;
import models.plants.PlantData;
import models.plants.PlantRepository;
import models.seasons.Season;

import java.util.Arrays;

public class Quest implements Comparable<Quest> {
    private QuestData questData;
    private String questName;
    private Priority priority;
    private QuestObjective objective;
    private RewardType reward;
    private int rewardAmount;
    public boolean isCompleted;
    private QuestCategory category;
    private String missionDescription;
    private int currentValue;
    private int[] targetValue;
    private transient Season season;
    private transient Level currentLevel;
    private transient PlantData targetPlant;
    private String targetPlantId;
    private transient PlantData rewardPlant;
    private String rewardPlantId;
    private String targetSeasonName;

    public Quest(QuestData questData) {
        this.questData = questData;
        this.questName = questData.getQuestName();
        this.priority = questData.getPriority();
        this.objective = questData.getObjective();
        this.reward = questData.getReward();
        this.rewardAmount = questData.getRewardAmount();
        this.category = questData.getCategory();
        this.missionDescription = questData.getConditionText();
        this.currentValue = 0;
        this.targetValue = Arrays.copyOf(questData.getTargetValue(), questData.getTargetValue().length);
    }

    public void ensureInitialized() {
        if (questData != null) {
            if (questName == null) {
                questName = questData.getQuestName();
            }
            if (priority == null) {
                priority = questData.getPriority();
            }
            if (objective == null) {
                objective = questData.getObjective();
            }
            if (reward == null) {
                reward = questData.getReward();
            }
            if (category == null) {
                category = questData.getCategory();
            }
            if (missionDescription == null) {
                missionDescription = questData.getConditionText();
            }
            if (targetValue == null || targetValue.length == 0) {
                targetValue = Arrays.copyOf(questData.getTargetValue(), questData.getTargetValue().length);
            }
        }
        if (targetPlant == null && targetPlantId != null) {
            targetPlant = PlantRepository.getInstance().findById(targetPlantId);
        }
        if (rewardPlant == null && rewardPlantId != null) {
            rewardPlant = PlantRepository.getInstance().findById(rewardPlantId);
        }
        if (targetPlant != null && targetPlantId == null) {
            targetPlantId = targetPlant.getId();
        }
        if (rewardPlant != null && rewardPlantId == null) {
            rewardPlantId = rewardPlant.getId();
        }
    }

    @Override
    public int compareTo(Quest other) {
        ensureInitialized();
        other.ensureInitialized();
        if (priority == null || other.priority == null) {
            return 0;
        }
        int priorityComparison = Integer.compare(priority.getRank(), other.priority.getRank());
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        return getQuestName().compareToIgnoreCase(other.getQuestName());
    }

    public QuestData getQuestData() {
        return questData;
    }

    public String getQuestName() {
        ensureInitialized();
        return questName;
    }

    public Priority getPriority() {
        ensureInitialized();
        return priority;
    }

    public QuestObjective getObjective() {
        ensureInitialized();
        return objective;
    }

    public RewardType getReward() {
        ensureInitialized();
        return reward;
    }

    public void setReward(RewardType reward) {
        this.reward = reward;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(int rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public QuestCategory getCategory() {
        ensureInitialized();
        return category;
    }

    public String getMissionDescription() {
        ensureInitialized();
        return missionDescription;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = Math.max(0, currentValue);
    }

    public void addProgress(int amount) {
        setCurrentValue(currentValue + amount);
    }

    public int[] getTargetValue() {
        ensureInitialized();
        return targetValue;
    }

    public int getSelectedTarget() {
        int[] values = getTargetValue();
        return values.length == 0 ? 0 : values[0];
    }

    public void setTargetValue(int[] targetValue) {
        this.targetValue = targetValue == null ? new int[0] : Arrays.copyOf(targetValue, targetValue.length);
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }

    public PlantData getTargetPlant() {
        ensureInitialized();
        return targetPlant;
    }

    public void setTargetPlant(PlantData targetPlant) {
        this.targetPlant = targetPlant;
        this.targetPlantId = targetPlant == null ? null : targetPlant.getId();
    }

    public String getTargetSeasonName() {
        return targetSeasonName;
    }

    public void setTargetSeasonName(String targetSeasonName) {
        this.targetSeasonName = targetSeasonName;
    }

    public PlantData getRewardPlant() {
        ensureInitialized();
        return rewardPlant;
    }

    public void setRewardPlant(PlantData rewardPlant) {
        this.rewardPlant = rewardPlant;
        this.rewardPlantId = rewardPlant == null ? null : rewardPlant.getId();
    }

    public void reset() {
        currentValue = 0;
        isCompleted = false;
    }
}
