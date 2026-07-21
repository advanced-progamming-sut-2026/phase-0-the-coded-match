package models.zombies;

import controllers.GameManagerController;
import controllers.QuestController;
import enums.*;
import models.*;
import models.GameMapRelated.GameMap;
import models.GameMapRelated.GameMapData;
import models.GameMapRelated.Tile;
import models.factories.ZombieBehaviorFactory;
import models.plants.Plant;
import models.zombies.strategies.ZombieBehavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Zombie implements Update {
    private ZombieData data;
    private int difficultyLevel;
    private int currentHp;
    private int eatDPS;
    private ZombieState currentState;
    private double x;
    private int y;
    private List<ZombieArmor> armors;
    private ZombieBehavior behavior;
    private List<ZombieEffect> effects;
    private boolean hasThrownImp;
    private double runningSpeed;
    private boolean wasRunning;
    private boolean hasParasol;
    private int stolenSuns;
    private int abilityTickTimer = 0;
    private boolean abilityDone;
    private List<Sun> stolenActiveSuns;
    private boolean isSubmerged;
    private boolean isFrozen;
    private int frozenTimer;

    public Zombie(ZombieData data, double x, int y) {
        this.data = data;
        this.difficultyLevel = App.getCurrentUser().getDifficultyLevel();
        this.currentHp = (int) (data.getMaxHP() * (difficultyLevel / 3.0));
        this.eatDPS = (int) (data.getEatDPS() * (difficultyLevel / 3.0));
        this.currentState = data.getState();
        this.x = x;
        this.y = y;
        this.effects = new ArrayList<>();
        this.hasThrownImp = false;
        this.runningSpeed = data.getRunningSpeed();
        this.wasRunning = false;
        this.hasParasol = data.isHasParasol(); //TODO: Parasol Zombie's talent should be handled in the method in which zombies are attacked and takes damage
        this.abilityDone = false;
        this.stolenActiveSuns = new ArrayList<>();
        this.isSubmerged = false;
        this.isFrozen = false;

        if (!data.getArmors().isEmpty()) {
            this.armors = new ArrayList<>();
            for (ZombieArmorData armorData : data.getArmors()) {
                this.armors.add(new ZombieArmor(armorData));
            }
        }

        this.behavior = ZombieBehaviorFactory.getBehavior(data.getBehaviorType());
    }

    @Override
    public void update() {
       if(isFrozen){
           return;
       }

        Plant target = GameManagerController.getInstance().getCurrentLevel().getFrontMostPlantInRow(this.y);
        if (data.getId().equalsIgnoreCase("ZombieIceAgeDodo")) {
            target = GameManagerController.getInstance().getCurrentLevel().getPlantInFrontOfZombie(this);
        }

        if (target != null && isAdjacentTo(target)){
            if (currentState == ZombieState.RUNNING) {
                wasRunning = true;
            }
            currentState = ZombieState.EATING;
        } else {
            if (currentState != ZombieState.RUNNING) {//TODO: and if not paralyzed
                currentState = ZombieState.WALKING;
            }
        }

        if (behavior != null) {
            behavior.updateZombie(this, target);
        }

    }

    private boolean isAdjacentTo(Plant target){
        if (this.x == target.getX() && this.y == target.getY()){ //TODO: Maybe later on change the condition to be more flexible
            return true;
        }
        return false;
    }

    public void walk() {
        if (currentState == ZombieState.EATING) {
            return;
        }
        x -= data.getSpeed();
    }

    public void run() {
        x -= runningSpeed;
    }

    public void attack(Plant plant) {
        if (this.getData().getId().equalsIgnoreCase("ZombieArmZombieNewspaper")) {
            plant.takeDamage(data.getEatDPS() * 2);
        } else {
            plant.takeDamage(data.getEatDPS());
        }
    }

    public void destroyPlant(Plant plant) {
        plant.setCurrentHp(0);
    }

    public void destroyZombie(Zombie zombie) {
        zombie.setCurrentHp(0);
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().remove(zombie);
    }

    public int stealSuns() {
        int sunsToSteal = Math.max(25, GameManagerController.getInstance().getCurrentLevel().getCollectedSunsAmount());
        if (sunsToSteal > 0) {
            Level level = GameManagerController.getInstance().getCurrentLevel();
            level.setCollectedSunsAmount(level.getCollectedSunsAmount() - sunsToSteal);
        }
        return sunsToSteal;
    }

    public void lazer() {
        GameMap map = GameManagerController.getInstance().getCurrentLevel().getGameMap();

        for (int i = 0; i < map.getColumns(); i++) {
            for (int j = 0; j < map.getRows(); j++) {
                if (j == y && x - i <= 4) {
                    Tile tile = map.getTile(j, i);
                    if (!tile.isEmpty()) {
                        tile.getPlant().setCurrentHp(0);
                    }
                }
            }
        }
    }

    public void takeDamage(int damage, Plant killerPlant) {
        if (data.getDisplayName().equalsIgnoreCase("knight zombie") && !armors.isEmpty()) {
            int remainingDamage = armors.get(armors.size() - 1).takeDamage(this ,damage);
            if (remainingDamage > 0) {
                if (armors.size() == 2) {
                    remainingDamage = armors.get(armors.size() - 2).takeDamage(this, remainingDamage);
                } else {
                    currentHp = Math.max(0, currentHp - remainingDamage);
                }
            }
        } else if (!armors.isEmpty()) {
            int remainingDamage = armors.get(0).takeDamage(this ,damage);
            if (remainingDamage > 0) {
                currentHp = Math.max(0, currentHp - remainingDamage);
            }
        } else {
            currentHp = Math.max(0, currentHp - damage);
        }
        if (isDead()) {
            Level level = GameManagerController.getInstance().getCurrentLevel();
            if (data.getId().matches("ZombieCrystalSkull")) {
                level.setCollectedSunsAmount(level.getCollectedSunsAmount() + (int) (stolenSuns / 2));
            } else if (data.getId().matches("ZombieRa")) {
                for (Sun sun : stolenActiveSuns)
                level.getActiveSuns().add(sun);
            }
            QuestController.notifyZombieKilled(level.getCurrentSeason());
            QuestController.onZombieDefeated(killerPlant);
            GameManagerController.getInstance().getCurrentLevel().getActiveZombies().remove(this);
        }
    }

    public void spawnImp(double x) {
        ZombieData impData = ZombieRepository.getInstance().findByDisplayName("Imp");
        Zombie newImp = new Zombie(impData, x, this.y);
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(newImp);
    }

    public void explodeDynamite() {
        y = 1;
        currentState = ZombieState.WALKING_BACKWARD;
    }

    public void walkBackWard() {
        x += data.getSpeed();
    }

    public void shuffleZombies(Zombie pianist) {
        for (Zombie zombie : GameManagerController.getInstance().getCurrentLevel().getActiveZombies()) {
            if (zombie != pianist) {
                //TODO: complete this
            }
        }
    }

    public void stealDroppedSuns() {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        for (Sun sun : level.getActiveSuns()) {
            //TODO: the sun must have a speed?
            level.getActiveSuns().remove(sun);
            stolenActiveSuns.add(sun);
        }
    }

    public void burn(Plant targetPlant) {
        if (x - targetPlant.getX() <= 1) {
            destroyPlant(targetPlant);
        }
    }

    public void raiseTomb() {
        List<Tile> emptyTiles = getValidTilesForGrave();
        int gravesToSpawn = Math.min(2, emptyTiles.size());

        if (gravesToSpawn == 0) {
            return;
        }

        if (emptyTiles.isEmpty()) {
            return;
        }

        Collections.shuffle(emptyTiles);

        for (int i = 0; i < gravesToSpawn; i++) {
            Tile selectedTile = emptyTiles.get(i);
            selectedTile.setType(TileType.GRAVE);
        }
    }

    private List<Tile> getValidTilesForGrave() {
        List<Tile> validTiles = new ArrayList<>();
        GameMap map = GameManagerController.getInstance().getCurrentLevel().getGameMap();
        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getColumns(); col++) {
                Tile tile = map.getTile(row, col);

                if (tile == null) {
                    continue;
                }

                if (tile.getType() == TileType.NORMAL && tile.isEmpty()) {
                    validTiles.add(tile);
                }
            }
        }
        return validTiles;
    }

    public void fly(Plant target) {
        x = target.getX() + 1;//TODO: shift the zombie by one tile
    }

    public void shootProjectile() { //TODO: include projectile TYPE
        Projectile icyProjectile = new Projectile(x, y, data.getSpeed(), data.getEatDPS(), false, false, null);
        //TODO: what should the speed and damage amount be??
        GameManagerController.getInstance().getCurrentLevel().getActiveProjectiles().add(icyProjectile);
    }

    public void makeKnight() {
        List<Zombie> defaultZombies = getDefaultZombies();

        if (defaultZombies.isEmpty()) {
            return;
        }
        Collections.shuffle(defaultZombies);

        for (int i = 0; i < 1; i++) {
            Zombie target = defaultZombies.get(i);
            target.addArmor(ArmorType.SHOULDER_ARMOR);
            target.addArmor(ArmorType.CROWN);
        }
    }

    public List<Zombie> getDefaultZombies() {
        List<Zombie> zombies = new ArrayList<>();
        for (Zombie zombie : GameManagerController.getInstance().getCurrentLevel().getActiveZombies()) {
            if (zombie.getData().getId().equalsIgnoreCase("ZombieDefault")) {
                zombies.add(zombie);
            }
        }
        return zombies;
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public ZombieData getData() {
        return data;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getEatDPS() {
        return eatDPS;
    }

    public void setEatDPS(int eatDPS) {
        this.eatDPS = eatDPS;
    }

    public ZombieState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ZombieState currentState) {
        this.currentState = currentState;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {this.x = x;}

    public int getY() {
        return y;
    }

    public void setY(int y) {this.y = y;}

    public List<ZombieArmor> getArmors() {
        return armors;
    }

    public void addArmor(ArmorType type) {
        ZombieArmor armor = new ZombieArmor(new ZombieArmorData(type));
        armors.add(armor);
    }

    public List<ZombieEffect> getEffects() {
        return effects;
    }

    public int getWaveCost() {
        return data.getWaveCost();
    }

    public boolean isHasThrownImp() {
        return hasThrownImp;
    }

    public void setHasThrownImp(boolean hasThrownImp) {
        this.hasThrownImp = hasThrownImp;
    }

    public boolean isWasRunning() {
        return wasRunning;
    }

    public void setWasRunning(boolean wasRunning) {
        this.wasRunning = wasRunning;
    }

    public boolean isHasParasol() {
        return hasParasol;
    }

    public void setHasParasol(boolean hasParasol) {
        this.hasParasol = hasParasol;
    }

    public int getStolenSuns() {
        return stolenSuns;
    }

    public void setStolenSuns(int stolenSuns) {
        this.stolenSuns = stolenSuns;
    }

    public int getAbilityTickTimer() {
        return abilityTickTimer;
    }

    public void setAbilityTickTimer(int abilityTickTimer) {
        this.abilityTickTimer = abilityTickTimer;
    }

    public boolean isAbilityDone() {
        return abilityDone;
    }

    public void setAbilityDone(boolean abilityDone) {
        this.abilityDone = abilityDone;
    }

    public boolean isSubmerged() {
        return isSubmerged;
    }

    public void setSubmerged(boolean submerged) {
        isSubmerged = submerged;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public ZombieBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(ZombieBehavior behavior) {
        this.behavior = behavior;
    }
}