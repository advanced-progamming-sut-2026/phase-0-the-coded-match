package models;

import enums.QuestRelated.Priority;
import enums.QuestRelated.QuestCategory;
import enums.QuestRelated.QuestData;
import enums.QuestRelated.QuestObjective;
import enums.QuestRelated.RewardType;
import models.seasons.Season;

public class Quest {
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
    private Season season;
    private Level currentLevel;

    public Quest(QuestData questData){
        this.questData = questData;
        this.questName = questData.getQuestName();
        this.priority = questData.getPriority();
        this.objective = questData.getObjective();
        this.reward = questData.getReward();
        this.isCompleted = false;
        this.category = questData.getCategory();
        this.missionDescription = questData.getConditionText();
        this.currentValue = 0;
        this.targetValue = questData.getTargetValue();
    }


    public void updateProgress(int amount) {}
    public boolean checkCompletionStatus() { return false; }
    public void distributeReward(User player) {}

    public String getQuestName() {
        return questName;
    }

    public RewardType getReward() {
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

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public int[] getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int[] targetValue) {
        this.targetValue = targetValue;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }
}
