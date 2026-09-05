package PvZ2.APproject.models.GameMapRelated;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.PlantTag;
import PvZ2.APproject.enums.TileType;
import PvZ2.APproject.models.MiniGameRelated.VaseBreaker;
import PvZ2.APproject.models.Update;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

import java.util.ArrayList;
import java.util.List;

public class Tile implements Update {
    private int row;
    private int column;
    private TileType type;
    private int currentHp;
    private Plant plant;
    private VaseBreaker.Vase vase;
    private Plant lilyPadPlant;
    private List<Zombie> zombies;
    private boolean isGettingDamaged = false;
    private boolean isGrave = false;
    public enum GraveReward {
        NONE,
        SUN_50,
        PLANT_FOOD
    }
    private GraveReward graveReward = GraveReward.NONE;
    private boolean holdsNecromancyPotential = false;
    private boolean slippery;
    private float burningTimer;

    public Tile(int row, int column, TileType type) {
        this.row = row;
        this.column = column;
        this.type = type;
        this.currentHp = type.getMaxHp();
        this.zombies = new ArrayList<>();
    }

    public Tile(Tile source) {
        this.row = source.row;
        this.column = source.column;
        this.type = source.type;
        this.currentHp = source.currentHp;
        this.isGrave = source.isGrave;
        this.graveReward = source.graveReward;
        this.holdsNecromancyPotential = source.holdsNecromancyPotential;
        this.slippery = source.slippery;
        this.burningTimer = source.burningTimer;
        this.zombies = new ArrayList<>();
    }

    public void takeDamage(int damage) {
        if (currentHp > 0) {
            currentHp -= damage;
            if (currentHp <= 0) {
                currentHp = 0;
                if(this.type == TileType.GRAVE){
                    destroyGrave();
                }
                this.type = TileType.NORMAL;
                this.isGrave = false;
            }
        }
    }

    private void destroyGrave(){
        switch (this.getGraveReward()) {
            case SUN_50:
                GameManagerController.getInstance().getCurrentLevel().setCollectedSunsAmount(GameManagerController.getInstance().getCurrentLevel().getCollectedSunsAmount()+50);
                break;

            case PLANT_FOOD:
                GameManagerController.getInstance().getCurrentLevel().setPlantFoodCount(GameManagerController.getInstance().getCurrentLevel().getPlantFoodCount()+1);
                System.out.println("Grave dropped 1 Plant Food!");
                break;

            case NONE:
            default:
                break;
        }
        this.setGrave(false, GraveReward.NONE);
    }

    public void startTakingDamage() {
        if (type == TileType.ICE && firePlantExists()) {
            currentHp -= 60;
            this.isGettingDamaged = true;
        }
    }

    public void stopTakingDamage() {
        if (type == TileType.ICE && currentHp <= 0) {
            currentHp = 0;
            this.isGettingDamaged = false;
            this.type = TileType.NORMAL;
        }
    }

    public boolean firePlantExists() {
        for (Plant p : GameManagerController.getInstance().getCurrentLevel().getActivePlants()) {
            int dx = Math.abs(p.getX() - column);
            int dy = Math.abs(p.getY() - row);
            if (p.hasThisTag(PlantTag.FIRE) && dx <= 1 && dy <= 1) {
                return true;
            }
        }
        return false;
    }
    public boolean isPlantable(Plant plant) {
        if (isBurning()) return false;
        if (!this.getType().isCanPlant()) {
            return false;
        }
        if (this.getType() == TileType.WATER) {
            if (plant.getData().getName().equalsIgnoreCase("LilyPad")) {
                return this.lilyPadPlant == null && this.getPlant() == null;
            }
            if (plant.hasThisTag(PlantTag.WATER)) return this.getPlant() == null;
            return this.lilyPadPlant != null && this.getPlant() == null;
        }
        return this.getPlant() == null && !isGrave();
    }

    public boolean isEmpty() {
        return plant == null;
    }

    public void setPlant(Plant plant) {
        if(isPlantable(plant)) {
            this.plant = plant;
        }
    }

    public Plant getPlant() {
        return plant;
    }

    public void removePlant() {
        this.plant = null;
    }

    @Override
    public void update(float delta) {
        startTakingDamage();
        stopTakingDamage();
        if (burningTimer > 0f) burningTimer = Math.max(0f, burningTimer - delta);
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
        this.type = type;
        if (type == TileType.GRAVE) {
            this.isGrave = true;
            if (currentHp <= 0) currentHp = type.getMaxHp();
        } else if (this.isGrave) {
            this.isGrave = false;
            this.graveReward = GraveReward.NONE;
        }
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public boolean isGettingDamaged() {
        return isGettingDamaged;
    }

    public List<Zombie> getZombies() {
        return zombies;
    }

    public void setGrave(boolean isGrave){
        this.isGrave = isGrave;
        if (isGrave) {
            this.type = TileType.GRAVE;
            if (currentHp <= 0) currentHp = TileType.GRAVE.getMaxHp();
        } else if (this.type == TileType.GRAVE) {
            this.type = TileType.NORMAL;
            this.graveReward = GraveReward.NONE;
        }
    }

    public boolean isGrave(){return isGrave;}
    public GraveReward getGraveReward() { return graveReward; }
    public boolean holdsNecromancyPotential() { return holdsNecromancyPotential; }
    public void setNecromancyPotential(boolean potential){
        holdsNecromancyPotential = potential;
    }

    public void setGrave(boolean active, GraveReward reward) {
        this.isGrave = active;
        this.graveReward = reward == null ? GraveReward.NONE : reward;
        if (active) {
            this.type = TileType.GRAVE;
            this.currentHp = TileType.GRAVE.getMaxHp();
        } else if (this.type == TileType.GRAVE) {
            this.type = TileType.NORMAL;
        }
    }


    public VaseBreaker.Vase getVase() {return vase;}

    public void setVase(VaseBreaker.Vase vase) {this.vase = vase;}

    public boolean hasVase() {return vase != null && !vase.isBroken();}

    public void removeVase() {this.vase = null;}

    public Plant getLilyPadPlant() { return lilyPadPlant; }

    public void setLilyPadPlant(Plant lilyPad) { this.lilyPadPlant = lilyPad; }

    public boolean isSlippery() {
        return slippery;
    }

    public void setSlippery(boolean slippery) {
        this.slippery = slippery;
    }

    public void setBurning(float duration) {
        burningTimer = Math.max(burningTimer, Math.max(0f, duration));
    }

    public boolean isBurning() {
        return burningTimer > 0f;
    }

    public float getBurningTimer() {
        return burningTimer;
    }
}
