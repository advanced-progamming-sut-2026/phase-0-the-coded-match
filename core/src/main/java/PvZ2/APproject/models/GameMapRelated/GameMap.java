package PvZ2.APproject.models.GameMapRelated;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.QuestController;
import PvZ2.APproject.enums.TileType;
import PvZ2.APproject.models.zombies.Zombie;

import java.util.ArrayList;
import java.util.List;

public final class GameMap {

    private int rows;
    private int columns;
    private int length;
    private int width;
    private String defaultPathDirection;
    private List<Tile> tiles;
    private transient Tile[][] grid;
    private List<Lawnmower> lawnmowers = new ArrayList<>();

    public GameMap(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.length = columns;
        this.width = rows;
        initializeGrid();
        initializeLawnMowers(rows);
    }

    public GameMap(GameMap source) {
        this.rows = source.rows;
        this.columns = source.columns;
        this.length = source.length;
        this.width = source.width;
        this.defaultPathDirection = source.defaultPathDirection;
        source.initializeGrid();
        this.tiles = new ArrayList<>();
        for (int r = 0; r < source.rows; r++) {
            for (int c = 0; c < source.columns; c++) {
                this.tiles.add(new Tile(source.grid[r][c]));
            }
        }
        initializeGrid();
        initializeLawnMowers(rows);
    }

    public GameMap copy() {
        return new GameMap(this);
    }

    public void initializeGrid() {
        if (rows <= 0) rows = 5;
        if (columns <= 0) columns = 9;
        this.grid = new Tile[rows][columns];

        if (tiles != null && !tiles.isEmpty()) {
            for (Tile tile : tiles) {
                int r = tile.getRow() - 1;
                int c = tile.getColumn() - 1;
                if (r >= 0 && r < rows && c >= 0 && c < columns) grid[r][c] = tile;
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (grid[r][c] == null) {
                    grid[r][c] = new Tile(r + 1, c + 1, TileType.NORMAL);
                }
            }
        }
    }

    public void initializeLawnMowers(int rows) {
        if (lawnmowers == null) {
            lawnmowers = new ArrayList<>();
        } else {
            lawnmowers.clear();
        }
        for (int row = 1; row <= rows; row++) {
            lawnmowers.add(new Lawnmower(row));
        }
    }

    public void handleLawnMower(Zombie zombie){
        int row = zombie.getY();
        Lawnmower mower = findLawnMower(row);
        if (mower == null || mower.HasBeenUsed()) {
            GameManagerController.getInstance().gameOver();
            return;
        }
        if (mower.isTriggered()) {
            zombie.takeDamage(Math.max(1, zombie.getCurrentHp() + zombie.getMaxHp()), null);
            QuestController.notifyZombiesKilledByLawnmower(1);
            return;
        }

        mower.trigger();

        List<Zombie> killed = new ArrayList<>();
        List<Zombie> activeZombies = GameManagerController.getInstance().getCurrentLevel().getActiveZombies();
        for (Zombie zombieInRow : new ArrayList<>(activeZombies)) {
            if (zombieInRow.getY() == row) {
                killed.add(zombieInRow);
                zombieInRow.takeDamage(Math.max(1, zombieInRow.getCurrentHp() + zombieInRow.getMaxHp()), null);
            }
        }
        QuestController.notifyZombiesKilledByLawnmower(killed.size());
    }

    public Lawnmower lawnMowerUsed(int row){
        Lawnmower mower = findLawnMower(row);
        if (mower == null || mower.HasBeenUsed() || mower.isTriggered()) {
            return null;
        }
        return mower;
    }

    private Lawnmower findLawnMower(int row) {
        for (Lawnmower mower : lawnmowers) {
            if (mower.getRow() == row) {
                return mower;
            }
        }
        return null;
    }

    private String getPlantNameAt(int col, int row){
        if(grid[row][col].getPlant() != null){
            return grid[row][col].getPlant().getData().getName();
        }
        return null;
    }

    public boolean checkGardenSymmetry() {
        if (grid == null) initializeGrid();
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows / 2; row++) {
                String first = getPlantNameAt(column, row);
                String mirrored = getPlantNameAt(column, rows - 1 - row);
                if (!isSamePlant(first, mirrored)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isSamePlant(String plantA, String plantB) {
        if (plantA == null && plantB == null) return true;  // Both tiles are empty (Symmetrical)
        if (plantA == null || plantB == null) return false; // One is empty, one has a plant (Asymmetrical)
        return plantA.equals(plantB);                       // Check if they are the exact same plant type
    }

    public Tile getTile(int x, int y) {
        if (grid == null) initializeGrid();
        if (x >= 1 && x <= columns && y >= 1 && y <= rows) {
            return grid[y - 1][x - 1];
        }
        return null;
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

    public Tile[][] getGrid() {
        return grid;
    }

    public void setGrid(Tile[][] grid) {
        this.grid = grid;
    }

    public List<Lawnmower> getLawnmowers() {
        return lawnmowers;
    }

    public void setLawnmowers(List<Lawnmower> lawnmowers) {
        this.lawnmowers = lawnmowers;
    }


}
