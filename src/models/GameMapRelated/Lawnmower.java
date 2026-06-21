package models.GameMapRelated;

public class  Lawnmower {

    int row;
    boolean hasBeenUsed;

    public static void trigger() {

    }

    public boolean HasBeenUsed() {
        return hasBeenUsed;
    }

    public void setHasBeenUsed(boolean hasBeenUsed) {
        this.hasBeenUsed = hasBeenUsed;
    }

    public int getRow(){
        return row;
    }
}
