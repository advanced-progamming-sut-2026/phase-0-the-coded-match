package models.GameMapRelated;

import com.google.gson.annotations.SerializedName;
import controllers.GameManagerController;
import enums.PlantTag;
import enums.TileType;
import models.MiniGameRelated.VaseBreaker;
import models.Update;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.ArrayList;
import java.util.List;

public class Tile implements Update {
    @SerializedName(value = "row", alternate = {"y"})
    private int row;
    @SerializedName(value = "column", alternate = {"x"})
    private int column;
    private int tileWidth;
    private TileType type;
    @SerializedName(value = "currentHp", alternate = {"hp"})
    private int currentHp;
    private Plant plant;
    private VaseBreaker.Vase vase;
    private Plant lilyPadPlant;
    private List<Zombie> zombies;
    private boolean isGettingDamaged;
    private boolean isGrave;
    private GraveReward graveReward = GraveReward.NONE;
    private boolean holdsNecromancyPotential;
    private boolean frozenZombie;

    public enum GraveReward {
        NONE,
        SUN_50,
        PLANT_FOOD
    }

    public Tile(int row, int column, TileType type) {
        this.row = row;
        this.column = column;
        this.type = type;
        this.currentHp = type.getMaxHp();
        this.zombies = new ArrayList<>();
        this.isGrave = type == TileType.GRAVE;
    }

    public void initialize() {
        if (type == null) {
            type = TileType.NORMAL;
        }
        if (currentHp <= 0 && type.getMaxHp() > 0) {
            currentHp = type.getMaxHp();
        }
        if (zombies == null) {
            zombies = new ArrayList<>();
        }
        if (type == TileType.GRAVE) {
            isGrave = true;
        }
        if (graveReward == null) {
            graveReward = GraveReward.NONE;
        }
    }

    public void takeDamage(int damage) {
        if (currentHp <= 0) {
            return;
        }
        currentHp -= Math.max(0, damage);
        if (currentHp <= 0) {
            currentHp = 0;
            if (type == TileType.GRAVE) {
                destroyGrave();
            }
            type = TileType.NORMAL;
            isGrave = false;
        }
    }

    private void destroyGrave() {
        if (GameManagerController.getInstance().getCurrentLevel() != null) {
            if (graveReward == GraveReward.SUN_50) {
                GameManagerController.getInstance().getCurrentLevel().setCollectedSunsAmount(
                        GameManagerController.getInstance().getCurrentLevel().getCollectedSunsAmount() + 50);
            } else if (graveReward == GraveReward.PLANT_FOOD) {
                GameManagerController.getInstance().getCurrentLevel().setPlantFoodCount(
                        GameManagerController.getInstance().getCurrentLevel().getPlantFoodCount() + 1);
            }
        }
        graveReward = GraveReward.NONE;
    }

    public boolean firePlantExists() {
        if (GameManagerController.getInstance().getCurrentLevel() == null) {
            return false;
        }
        for (Plant p : GameManagerController.getInstance().getCurrentLevel().getActivePlants()) {
            int dx = Math.abs(p.getX() - column);
            int dy = Math.abs(p.getY() - row);
            if (p.hasThisTag(PlantTag.FIRE) && dx <= 1 && dy <= 1) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlantable(Plant candidate) {
        if (candidate == null || isGrave) {
            return false;
        }
        if (type == TileType.WATER) {
            String plantName = candidate.getData().getName() == null ? "" : candidate.getData().getName();
            boolean lilyPad = plantName.replace("_", " ").replace("-", " ").replaceAll("\\s+", " ")
                    .trim().equalsIgnoreCase("Lily Pad");
            if (lilyPad) {
                return lilyPadPlant == null;
            }
            if (candidate.hasThisTag(PlantTag.WATER)) {
                return plant == null;
            }
            return lilyPadPlant != null && plant == null;
        }
        if (!type.isCanPlant()) {
            return false;
        }
        if (plant == null) {
            return true;
        }
        return candidate.hasThisTag(PlantTag.STACK) || plant.hasThisTag(PlantTag.STACK);
    }

    public boolean isEmpty() {
        return plant == null;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Plant getPlant() {
        return plant;
    }

    public void removePlant() {
        this.plant = null;
    }

    @Override
    public void update() {
        if (type == TileType.ICE && firePlantExists()) {
            takeDamage(6);
            isGettingDamaged = true;
        } else {
            isGettingDamaged = false;
        }
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type == null ? TileType.NORMAL : type;
        if (currentHp <= 0 && this.type.getMaxHp() > 0) {
            currentHp = this.type.getMaxHp();
        }
        if (this.type != TileType.GRAVE) {
            isGrave = false;
        }
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public boolean isGettingDamaged() {
        return isGettingDamaged;
    }

    public List<Zombie> getZombies() {
        if (zombies == null) {
            zombies = new ArrayList<>();
        }
        return zombies;
    }

    public void setGrave(boolean grave) {
        setGrave(grave, grave ? graveReward : GraveReward.NONE);
    }

    public boolean isGrave() {
        return isGrave;
    }

    public GraveReward getGraveReward() {
        return graveReward;
    }

    public boolean holdsNecromancyPotential() {
        return holdsNecromancyPotential;
    }

    public void setHoldsNecromancyPotential(boolean value) {
        holdsNecromancyPotential = value;
    }

    public void setGrave(boolean active, GraveReward reward) {
        isGrave = active;
        graveReward = reward == null ? GraveReward.NONE : reward;
        if (active) {
            type = TileType.GRAVE;
            currentHp = TileType.GRAVE.getMaxHp();
        } else if (type == TileType.GRAVE) {
            type = TileType.NORMAL;
            currentHp = 0;
        }
    }

    public VaseBreaker.Vase getVase() {
        return vase;
    }

    public void setVase(VaseBreaker.Vase vase) {
        this.vase = vase;
    }

    public boolean hasVase() {
        return vase != null && !vase.isBroken();
    }

    public void removeVase() {
        vase = null;
    }

    public Plant getLilyPadPlant() {
        return lilyPadPlant;
    }

    public void setLilyPadPlant(Plant lilyPad) {
        lilyPadPlant = lilyPad;
    }

    public boolean hasFrozenZombie() {
        return frozenZombie;
    }

    public void setFrozenZombie(boolean frozenZombie) {
        this.frozenZombie = frozenZombie;
    }
}
