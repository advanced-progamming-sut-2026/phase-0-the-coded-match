package PvZ2.APproject.models.MiniGameRelated;

import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.LevelData;

public abstract class MiniGame extends Level {
    public boolean isGameOver;

    public MiniGame(LevelData data) {
        super(data);
    }

}
