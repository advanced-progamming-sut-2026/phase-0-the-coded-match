package models.GameMapRelated;

import enums.TileType;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private int rows;
    private int columns;
    private int length;
    private int width;
    private int zombieStartColumn;
    private int homeColumn;
    private String defaultPathDirection;
    private List<Tile> tiles;
    private transient Tile[][] grid;
    private transient ArrayList<Lawnmower> lawnmowers;

    public GameMap(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.zombieStartColumn = columns;
        initializeGrid();
    }

    public void initializeGrid() {
        if (rows <= 0) {
            rows = 5;
        }
        if (columns <= 0) {
            columns = 9;
        }
        if (zombieStartColumn <= 0) {
            zombieStartColumn = columns;
        }
        grid = new Tile[rows][columns];
        for (int y = 1; y <= rows; y++) {
            for (int x = 1; x <= columns; x++) {
                grid[y - 1][x - 1] = new Tile(y, x, TileType.NORMAL);
            }
        }
        if (tiles != null) {
            for (Tile tile : tiles) {
                if (tile == null) {
                    continue;
                }
                tile.initialize();
                int x = tile.getColumn();
                int y = tile.getRow();
                if (x >= 1 && x <= columns && y >= 1 && y <= rows) {
                    grid[y - 1][x - 1] = tile;
                }
            }
        }
        lawnmowers = new ArrayList<>();
        for (int y = 1; y <= rows; y++) {
            lawnmowers.add(new Lawnmower(y));
        }
    }

    private String getPlantNameAt(int x, int y) {
        Tile tile = getTile(x, y);
        if (tile != null && tile.getPlant() != null) {
            return tile.getPlant().getData().getName();
        }
        return null;
    }

    public boolean checkGardenSymmetry() {
        for (int x = 1; x <= columns; x++) {
            for (int y = 1; y <= rows / 2; y++) {
                if (!isSamePlant(getPlantNameAt(x, y), getPlantNameAt(x, rows - y + 1))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isSamePlant(String first, String second) {
        if (first == null && second == null) {
            return true;
        }
        return first != null && first.equals(second);
    }

    public Tile getTile(int x, int y) {
        if (grid == null) {
            initializeGrid();
        }
        if (x < 1 || x > columns || y < 1 || y > rows) {
            return null;
        }
        return grid[y - 1][x - 1];
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
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

    public Tile[][] getGrid() {
        if (grid == null) {
            initializeGrid();
        }
        return grid;
    }

    public void setGrid(Tile[][] grid) {
        this.grid = grid;
    }

    public ArrayList<Lawnmower> getLawnmowers() {
        if (lawnmowers == null) {
            initializeGrid();
        }
        return lawnmowers;
    }

    public void setLawnmowers(ArrayList<Lawnmower> lawnmowers) {
        this.lawnmowers = lawnmowers;
    }

    public List<Tile> getTiles() {
        return tiles;
    }
}
