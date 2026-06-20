package models.GameMapRelated;

import java.util.ArrayList;

public class GameMap {

    private int rows;
    private int columns;
    private int length;
    private int width;
    private Tile[][] tiles;
    private ArrayList<Lawnmower> lawnmowers;

    public GameMap(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }


}
