package Model;
import java.util.List;
import Enums.SeasonType;

public abstract class Season {
    protected String name;
    protected SeasonType type;

    protected List<Level> levels;

    protected Tile[][] field;

    public abstract void initializeGrid();

    public static Tile getTile() {

    }
}
