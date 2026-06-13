package models;

public abstract class MiniGame {
    private int playerSunAmount;
    private boolean isGameOver;

    public MiniGame(int playerSunAmount, boolean isGameOver){
        this.playerSunAmount = playerSunAmount;
        this.isGameOver=isGameOver;
    }

    public abstract void initializeStage();
    public abstract void processInteraction();
    public abstract void checkRules();
}
