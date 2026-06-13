package models;

import java.util.List;

public abstract class Tile {

    protected int row;
    protected int column;
    protected Plant plant;
    protected List<Zombie> zombies;

    public abstract boolean canPlant();

    public static boolean isEmpty() {

    }

    public static void removePlant() {

    }
}
