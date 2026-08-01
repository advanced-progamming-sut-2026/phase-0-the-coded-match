package models;

import java.util.HashSet;
import java.util.Set;

public class QuestRuntimeState {
    private int explosivePlantsPlacedThisLevel;
    private boolean brokeFamilyRule;
    private boolean usedForbiddenFamily;
    private boolean nightOnlyRuleBroken;
    private boolean nightPlantUsed;
    private int familyTargetKills;
    private int sunProducerPlantsPlacedThisLevel;
    private boolean cloudyDayRuleBroken;
    private boolean proPlantRuleBroken;
    private boolean onlyCactusRuleBroken;
    private double firstWaveStartTick = -1;
    private Set<Integer> plantedColumnsThisLevel = new HashSet<>();
    private Set<Integer> plantedRowsThisLevel = new HashSet<>();

    public void resetLevelState() {
        explosivePlantsPlacedThisLevel = 0;
        brokeFamilyRule = false;
        usedForbiddenFamily = false;
        nightOnlyRuleBroken = false;
        nightPlantUsed = false;
        familyTargetKills = 0;
        sunProducerPlantsPlacedThisLevel = 0;
        cloudyDayRuleBroken = false;
        proPlantRuleBroken = false;
        onlyCactusRuleBroken = false;
        firstWaveStartTick = -1;
        plantedColumnsThisLevel.clear();
        plantedRowsThisLevel.clear();
    }

    public int getExplosivePlantsPlacedThisLevel() {
        return explosivePlantsPlacedThisLevel;
    }

    public int incrementExplosivePlantsPlacedThisLevel() {
        return ++explosivePlantsPlacedThisLevel;
    }

    public boolean isBrokeFamilyRule() {
        return brokeFamilyRule;
    }

    public void setBrokeFamilyRule(boolean brokeFamilyRule) {
        this.brokeFamilyRule = brokeFamilyRule;
    }

    public boolean isUsedForbiddenFamily() {
        return usedForbiddenFamily;
    }

    public void setUsedForbiddenFamily(boolean usedForbiddenFamily) {
        this.usedForbiddenFamily = usedForbiddenFamily;
    }

    public boolean isNightOnlyRuleBroken() {
        return nightOnlyRuleBroken;
    }

    public void setNightOnlyRuleBroken(boolean nightOnlyRuleBroken) {
        this.nightOnlyRuleBroken = nightOnlyRuleBroken;
    }

    public boolean isNightPlantUsed() {
        return nightPlantUsed;
    }

    public void setNightPlantUsed(boolean nightPlantUsed) {
        this.nightPlantUsed = nightPlantUsed;
    }

    public int getFamilyTargetKills() {
        return familyTargetKills;
    }

    public int incrementFamilyTargetKills() {
        return ++familyTargetKills;
    }

    public int getSunProducerPlantsPlacedThisLevel() {
        return sunProducerPlantsPlacedThisLevel;
    }

    public int incrementSunProducerPlantsPlacedThisLevel() {
        return ++sunProducerPlantsPlacedThisLevel;
    }

    public boolean isCloudyDayRuleBroken() {
        return cloudyDayRuleBroken;
    }

    public void setCloudyDayRuleBroken(boolean cloudyDayRuleBroken) {
        this.cloudyDayRuleBroken = cloudyDayRuleBroken;
    }

    public boolean isProPlantRuleBroken() {
        return proPlantRuleBroken;
    }

    public void setProPlantRuleBroken(boolean proPlantRuleBroken) {
        this.proPlantRuleBroken = proPlantRuleBroken;
    }

    public boolean isOnlyCactusRuleBroken() {
        return onlyCactusRuleBroken;
    }

    public void setOnlyCactusRuleBroken(boolean onlyCactusRuleBroken) {
        this.onlyCactusRuleBroken = onlyCactusRuleBroken;
    }

    public double getFirstWaveStartTick() {
        return firstWaveStartTick;
    }

    public void setFirstWaveStartTick(double firstWaveStartTick) {
        this.firstWaveStartTick = firstWaveStartTick;
    }

    public Set<Integer> getPlantedColumnsThisLevel() {
        if (plantedColumnsThisLevel == null) {
            plantedColumnsThisLevel = new HashSet<>();
        }
        return plantedColumnsThisLevel;
    }

    public Set<Integer> getPlantedRowsThisLevel() {
        if (plantedRowsThisLevel == null) {
            plantedRowsThisLevel = new HashSet<>();
        }
        return plantedRowsThisLevel;
    }
}
