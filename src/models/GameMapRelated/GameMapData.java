package models.GameMapRelated;

import models.TileData;

import java.util.List;

public class GameMapData {
    private int rows;
    private int columns;
    private int zombieStartColumn;
    private int homeColumn;
    private String defaultPathDirection;
    private List<TileData> tiles;

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getZombieStartColumn() {
        return zombieStartColumn;
    }

    public int getHomeColumn() {
        return homeColumn;
    }

    public String getDefaultPathDirection() {
        return defaultPathDirection;
    }

    public List<TileData> getTiles() {
        return tiles;
    }
}