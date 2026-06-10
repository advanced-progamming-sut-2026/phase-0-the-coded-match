package Model;
import java.util.List;
import Enums.SeasonType;

public abstract class Season {

    protected SeasonData data;
    protected String name;
    protected SeasonType type;
    protected List<Level> levels;
    protected Tile[][] field;

    public void initializeGrid() {

    };

    public Tile getTile(int x, int y) {
        return null;
    }

    public Season(SeasonData data) {
        this.data = data;
    }

    public SeasonData getData() {
        return data;
    }

    public List<LevelData> getLevels() {
        return data.getLevels();
    }

    public List<SpecialFeatureData> getSpecialFeatures() {
        return data.getSpecialFeatures();
    }

    public abstract void applySpecialRules();

}

