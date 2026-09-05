package PvZ2.APproject.models.GameMapRelated;

public class Lawnmower {
    private int row;
    private boolean hasBeenUsed;
    private boolean triggered;

    public Lawnmower(int row) { this.row = row; }
    public void trigger() { triggered = true; }
    public boolean hasBeenUsed() { return hasBeenUsed; }
    public void setHasBeenUsed(boolean used) { hasBeenUsed = used; }
    public boolean isTriggered() { return triggered; }
    public int getRow(){ return row; }
}
