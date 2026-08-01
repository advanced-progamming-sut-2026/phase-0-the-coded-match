package models.GameMapRelated;

import java.util.List;

public class GameMapData {
    private int rows;
    private int columns;
    private int length;
    private int width;
    private int zombieStartColumn;
    private int homeColumn;
    private String defaultPathDirection;
    private List<Tile> tiles;

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

    public List<Tile> getTiles() {
        return tiles;
    }

    public Tile getTile(int row, int column) {
        for (Tile tile : tiles) {
            if (tile.getRow() == row && tile.getColumn() == column) {
                return tile;
            }
        }
        return null;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }
}