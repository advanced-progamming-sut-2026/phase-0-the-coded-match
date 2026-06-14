package models;

import enums.QuestCategory;
import enums.QuestObjective;
import enums.RewardType;

public class Quest {
    private String questName;
    private String priority;
    private QuestObjective objective;
    private RewardType reward;
    public boolean isCompleted;
    private QuestCategory category;
    private String missionDescription;
    private int currentValue;
    private int targetValue;

    public Quest(String questName, String priority, QuestObjective objective, RewardType reward, boolean isCompleted,
                 QuestCategory category, String missionDescription, int currentValue, int targetValue){

    }


    public void updateProgress(int amount) {}
    public boolean checkCompletionStatus() { return false; }
    public void distributeReward(User player) {}
}
