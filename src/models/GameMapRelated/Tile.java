package models.GameMapRelated;

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
    private int row;
    private int column;
    private int tileWidth; //TODO: choose an optional width?
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

    public Tile(int row, int column, TileType type) {
        this.row = row;
        this.column = column;
        this.type = type;
        this.currentHp = type.getMaxHp();
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
        if (this.getType() == TileType.WATER) {
            if (plant.getData().getName().equalsIgnoreCase("LilyPad") || plant.hasThisTag(PlantTag.WATER)) {
                return this.getPlant() == null; // Can plant if empty
            }
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
    public void update() {
        startTakingDamage();
        stopTakingDamage();
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

    public void setGrave(boolean isGrave){this.isGrave = isGrave;}

    public boolean isGrave(){return isGrave;}
    public GraveReward getGraveReward() { return graveReward; }
    public boolean holdsNecromancyPotential() { return holdsNecromancyPotential; }

    public void setGrave(boolean active, GraveReward reward) {
        this.isGrave = active;
        this.graveReward = reward;
        this.setType(TileType.GRAVE);
    }


    public VaseBreaker.Vase getVase() {return vase;}

    public void setVase(VaseBreaker.Vase vase) {this.vase = vase;}

    public boolean hasVase() {return vase != null && !vase.isBroken();}

    public void removeVase() {this.vase = null;}

    public Plant getLilyPadPlant() { return lilyPadPlant; }

    public void setLilyPadPlant(Plant lilyPad) { this.lilyPadPlant = lilyPad; }
}