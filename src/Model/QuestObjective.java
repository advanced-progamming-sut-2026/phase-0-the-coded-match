package model;

public abstract class QuestObjective {
    private int targetValue;
    private int currentValue;
    public boolean verifyCondition;

    public QuestObjective(int targetValue, int currentValue, boolean verifyCondition){
        this.targetValue=targetValue;
        this.currentValue=currentValue;
        this.verifyCondition= verifyCondition;
    }
}
