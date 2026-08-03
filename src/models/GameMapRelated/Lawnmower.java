package models.GameMapRelated;

public class Lawnmower {
    private int row;
    private boolean hasBeenUsed;

    public Lawnmower(int row) { this.row = row; }
    public static void trigger() {}
    public boolean HasBeenUsed() { return hasBeenUsed; }
    public void setHasBeenUsed(boolean used) { hasBeenUsed = used; }
    public int getRow(){ return row; }
}
