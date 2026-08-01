package models.GameMapRelated;

public class Lawnmower {
    private final int row;
    private boolean hasBeenUsed;

    public Lawnmower(int row) {
        this.row = row;
    }

    public static void trigger() {
    }

    public boolean HasBeenUsed() {
        return hasBeenUsed;
    }

    public void setHasBeenUsed(boolean hasBeenUsed) {
        this.hasBeenUsed = hasBeenUsed;
    }

    public int getRow() {
        return row;
    }
}
