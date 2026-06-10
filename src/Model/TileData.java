package Model;

public class TileData {
    private int x;
    private int y;
    private String type;
    private int hp;
    private boolean plantable;
    private boolean walkable;
    private String direction;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getType() {
        return type;
    }

    public int getHp() {
        return hp;
    }

    public boolean isPlantable() {
        return plantable;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public String getDirection() {
        return direction;
    }
}