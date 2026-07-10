package models;

import enums.SpecialLevelType;

public class SpecialLevelRuleData {
    private SpecialLevelType type;
    private String value;
    private String description;
    private boolean enabled;

    public SpecialLevelType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}