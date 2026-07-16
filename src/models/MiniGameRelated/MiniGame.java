package models.MiniGameRelated;

public abstract class MiniGame {
    public int playerSunAmount;
    public boolean isGameOver;

    public abstract void initializeStage();
    public abstract void processInteraction();
    public abstract void checkRules();
}
