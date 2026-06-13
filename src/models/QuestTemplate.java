package models;

import enums.QuestCategory;

public class QuestTemplate {
    private String questName;
    private QuestCategory category;
    private String rawConditionText;
    private String rawRewardFormula;
    private String priority;
    private String variablesPool;

    public QuestTemplate(String questName, String categoryStr, String rawConditionText,
                         String rawRewardFormula, String priority, String variablesPool) {}
}
