package models.MiniGameRelated;

import models.Level;
import models.LevelData;

public abstract class MiniGame extends Level {
    public int playerSunAmount;
    public boolean isGameOver;

    public MiniGame(LevelData data) {
        super(data);
    }

}
