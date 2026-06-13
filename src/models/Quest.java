package models;

import enums.QuestCategory;

public class Quest {
    private String questName;
    private String priority;
    private QuestObjective objective;
    private Reward reward;
    public boolean isCompleted;
    private QuestCategory category;
    private String missionDescription;
    private int currentValue;
    private int targetValue;

    public Quest(String questName, String priority, QuestObjective objective, Reward reward, boolean isCompleted,
                 QuestCategory category, String missionDescription, int currentValue, int targetValue){

    }


    public void updateProgress(int amount) {}
    public boolean checkCompletionStatus() { return false; }
    public void distributeReward(User player) {}
}
