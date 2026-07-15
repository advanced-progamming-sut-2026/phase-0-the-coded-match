package models.GameMapRelated;

import enums.PlantTag;
import models.plants.Plant;

import java.util.ArrayList;

public class GameMap {

    private int rows;
    private int columns;
    private int length;
    private int width;
    private Tile[][] grid;
    private ArrayList<Lawnmower> lawnmowers;

    public GameMap(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        grid = new Tile[rows][columns];
    }

    private String getPlantNameAt(int col, int row){
        if(grid [col][row].getPlant() != null){
            return grid [col][row].getPlant().getData().getName();
        }
        return null;
    }

    public boolean checkGardenSymmetry() {
        int totalRows = 5;
        int totalColumns = 9;

        // Check every single column from left to right
        for (int x = 0; x < totalColumns; x++) {

            // 1. Check Row 1 vs Row 5 (index 0 vs index 4)
            String plantRow1 = getPlantNameAt(x, 0); // e.g., "Peashooter" or null
            String plantRow5 = getPlantNameAt(x, 4);

            if (!isSamePlant(plantRow1, plantRow5)) {
                return false; // Found a mismatch, garden is NOT symmetrical!
            }

            // 2. Check Row 2 vs Row 4 (index 1 vs index 3)
            String plantRow2 = getPlantNameAt(x, 1);
            String plantRow4 = getPlantNameAt(x, 3);

            if (!isSamePlant(plantRow2, plantRow4)) {
                return false; // Found a mismatch, garden is NOT symmetrical!
            }

            // Note: Row 3 (index 2) is the middle row, so it mirrors itself.
            // We do not need to check it!
        }

        return true; // If we checked everything and found no mismatches, it is perfectly symmetrical!
    }

    // Helper method to compare plant strings safely (handles null/empty tiles)
    private boolean isSamePlant(String plantA, String plantB) {
        if (plantA == null && plantB == null) return true;  // Both tiles are empty (Symmetrical)
        if (plantA == null || plantB == null) return false; // One is empty, one has a plant (Asymmetrical)
        return plantA.equals(plantB);                       // Check if they are the exact same plant type
    }

    public Tile getTile(int row, int column) {
        if (row >= 0 && row <= rows && column >= 0 && column <= columns) {
            return grid[row][column];
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
