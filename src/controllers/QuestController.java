package controllers;

import models.Quest;
import models.QuestTemplate;

public class QuestController {
    public void checkQuestObjectives(){}

    public String claimReward(){}

    public void loadTemplatesFromCSV(String filePath) {}

    public void generateDailyQuests() {}

    public void notifySunCollected(int amount) {}

    public void notifyZombieKilled(){}

    public Quest instantiateQuestFromTemplate(QuestTemplate template) {}
}
