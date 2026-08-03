package models;

import enums.QuestRelated.Priority;
import enums.QuestRelated.QuestCategory;
import enums.QuestRelated.QuestData;
import enums.QuestRelated.QuestObjective;
import enums.QuestRelated.RewardType;
import models.plants.PlantData;
import models.seasons.Season;

public class Quest implements Comparable<Quest> {
    private QuestData questData;
    private String questName;
    private Priority priority;
    private QuestObjective objective;
    private RewardType reward;
    private int rewardAmount;
    public boolean isCompleted;
    private boolean rewardClaimed;
    private int pendingRewardAmount;
    private QuestCategory category;
    private String missionDescription;
    private int currentValue;
    private int[] targetValue;
    private Season season;
    private Level currentLevel;
    private PlantData targetPlant;

    public Quest(QuestData questData){
        this.questData = questData;
        this.questName = questData.getQuestName();
        this.priority = questData.getPriority();
        this.objective = questData.getObjective();
        this.reward = questData.getReward();
        this.rewardAmount = questData.getRewardAmount();
        this.isCompleted = false;
        this.rewardClaimed = false;
        this.pendingRewardAmount = questData.getRewardAmount();
        this.category = questData.getCategory();
        this.missionDescription = questData.getConditionText();
        this.currentValue = 0;
        this.targetValue = questData.getTargetValue().clone();
    }

    @Override
    public int compareTo(Quest other) {
        if (this.priority == null || other.priority == null) {
            return 0;
        }
        return Integer.compare(this.priority.ordinal(), other.priority.ordinal());
    }

    public QuestData getQuestData() { return questData; }
    public String getQuestName() { return questName; }
    public RewardType getReward() { return reward; }
    public void setReward(RewardType reward) { this.reward = reward; }
    public int getRewardAmount() { return rewardAmount; }
    public void setRewardAmount(int rewardAmount) { this.rewardAmount = rewardAmount; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public boolean isRewardClaimed() { return rewardClaimed; }
    public void setRewardClaimed(boolean rewardClaimed) { this.rewardClaimed = rewardClaimed; }
    public int getPendingRewardAmount() { return pendingRewardAmount; }
    public void setPendingRewardAmount(int pendingRewardAmount) { this.pendingRewardAmount = pendingRewardAmount; }
    public int getCurrentValue() { return currentValue; }
    public void setCurrentValue(int currentValue) { this.currentValue = currentValue; }
    public int[] getTargetValue() { return targetValue; }
    public void setTargetValue(int[] targetValue) { this.targetValue = targetValue; }
    public Season getSeason() { return season; }
    public void setSeason(Season season) { this.season = season; }
    public PlantData getTargetPlant() { return targetPlant; }
    public void setTargetPlant(PlantData targetPlant) { this.targetPlant = targetPlant; }
}
