package models.GameMapRelated;

import enums.PlantTag;
import enums.TileType;
import models.plants.Plant;

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
    private ArrayList<Lawnmower> lawnmowers;

    public GameMap(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.length = columns;
        this.width = rows;
        initializeGrid();
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

    private String getPlantNameAt(int col, int row){
        if(grid[row][col].getPlant() != null){
            return grid[row][col].getPlant().getData().getName();
        }
        return null;
    }

    public boolean checkGardenSymmetry() {
        int totalRows = rows;
        int totalColumns = columns;

        for (int x = 0; x < totalColumns; x++) {

            String plantRow1 = getPlantNameAt(x, 0);
            String plantRow5 = getPlantNameAt(x, 4);

            if (!isSamePlant(plantRow1, plantRow5)) {
                return false;
            }

            String plantRow2 = getPlantNameAt(x, 1);
            String plantRow4 = getPlantNameAt(x, 3);

            if (!isSamePlant(plantRow2, plantRow4)) {
                return false;
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

    public ArrayList<Lawnmower> getLawnmowers() {
        return lawnmowers;
    }

    public void setLawnmowers(ArrayList<Lawnmower> lawnmowers) {
        this.lawnmowers = lawnmowers;
    }


}
