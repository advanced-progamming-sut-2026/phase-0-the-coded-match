package PvZ2.APproject.models.GameMapRelated;

import java.util.List;

public class GameMapData {
    private int rows;
    private int columns;
    private String defaultPathDirection;
    private List<Tile> tiles;

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
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


}
