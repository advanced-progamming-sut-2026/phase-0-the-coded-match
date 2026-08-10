package models;

import controllers.QuestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestsModel {
    private List<Quest> availableQuests;

    public QuestsModel() {
        this.availableQuests = new ArrayList<>();
    }

    public void sortByPriority() {
        Collections.sort(this.availableQuests);
    }

    public List<Quest> getAvailableQuests() {
        sortByPriority();
        return availableQuests;
    }

    public Quest getQuestByName(String name) {
        for (Quest quest : availableQuests) {
            if (quest.getQuestName().equalsIgnoreCase(name)) {
                return quest;
            }
        }
        return null;
    }

    public void addQuest(Quest quest) {
        availableQuests.add(quest);
    }

    public void setAvailableQuests(List<Quest> availableQuests) {
        this.availableQuests = availableQuests;
    }
}
