package models;

import enums.QuestRelated.Priority;
import enums.QuestRelated.QuestCategory;
import enums.QuestRelated.QuestObjective;
import enums.RewardType;

public class Quest {
    private String questName;
    private Priority priority;
    private QuestObjective objective;
    private RewardType reward;
    public boolean isCompleted;
    private QuestCategory category;
    private String missionDescription;
    private int currentValue;
    private int targetValue;

    public Quest(String questName, Priority priority, QuestObjective objective, RewardType reward, boolean isCompleted,
                 QuestCategory category, String missionDescription, int currentValue, int targetValue){

    }


    public void updateProgress(int amount) {}
    public boolean checkCompletionStatus() { return false; }
    public void distributeReward(User player) {}
}
