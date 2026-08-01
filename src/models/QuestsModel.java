package models;

import enums.QuestRelated.QuestCategory;
import enums.QuestRelated.QuestData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class QuestsModel {
    private List<Quest> availableQuests;

    public QuestsModel() {
        availableQuests = new ArrayList<>();
    }

    private void ensureInitialized() {
        if (availableQuests == null) {
            availableQuests = new ArrayList<>();
        }
        Set<QuestData> seen = EnumSet.noneOf(QuestData.class);
        Iterator<Quest> iterator = availableQuests.iterator();
        while (iterator.hasNext()) {
            Quest quest = iterator.next();
            if (quest == null || quest.getQuestData() == null || !seen.add(quest.getQuestData())) {
                iterator.remove();
                continue;
            }
            quest.ensureInitialized();
        }
    }

    public void synchronizeCatalog() {
        ensureInitialized();
        for (QuestData data : QuestData.values()) {
            if (getQuestByData(data) == null) {
                availableQuests.add(new Quest(data));
            }
        }
        ensureInitialized();
    }

    public void sortByPriority() {
        ensureInitialized();
        Collections.sort(availableQuests);
    }

    public List<Quest> getAvailableQuests() {
        sortByPriority();
        return availableQuests;
    }

    public List<Quest> getQuestsByCategory(QuestCategory category) {
        ensureInitialized();
        List<Quest> result = new ArrayList<>();
        for (Quest quest : availableQuests) {
            if (quest.getCategory() == category) {
                result.add(quest);
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<Quest> getCompletedQuests() {
        ensureInitialized();
        List<Quest> result = new ArrayList<>();
        for (Quest quest : availableQuests) {
            if (quest.isCompleted()) {
                result.add(quest);
            }
        }
        Collections.sort(result);
        return result;
    }

    public Quest getQuestByName(String name) {
        ensureInitialized();
        if (name == null) {
            return null;
        }
        for (Quest quest : availableQuests) {
            if (quest.getQuestName().equalsIgnoreCase(name.trim())) {
                return quest;
            }
        }
        return null;
    }

    public Quest getQuestByData(QuestData data) {
        ensureInitialized();
        if (data == null) {
            return null;
        }
        for (Quest quest : availableQuests) {
            if (quest.getQuestData() == data) {
                return quest;
            }
        }
        return null;
    }

    public void addQuest(Quest quest) {
        ensureInitialized();
        if (quest != null && quest.getQuestData() != null && getQuestByData(quest.getQuestData()) == null) {
            availableQuests.add(quest);
        }
    }

    public void setAvailableQuests(List<Quest> availableQuests) {
        this.availableQuests = availableQuests == null ? new ArrayList<>() : availableQuests;
        ensureInitialized();
    }
}
