package PvZ2.APproject.models.zombies;

import PvZ2.APproject.enums.SeasonType;
import PvZ2.APproject.enums.ZombieState;

import java.util.List;

public class ZombieData {
    private String id;
    private String displayName;
    private List<SeasonType> seasons;
    private int HP;
    private int eatDPS;
    private double speed;
    private double attackInterval;
    private int waveCost;
    private List<ZombieArmorData> armors;
    private String behaviorType;
    private ZombieState state;
    private double runningSpeed;
    private boolean hasParasol;
    private int cost; //for IZombie minigame
    private String path;

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<SeasonType> getSeasons() {
        return seasons;
    }

    public int getHP() {
        return HP;
    }

    public int getEatDPS() {
        return eatDPS;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAttackInterval() {
        return attackInterval;
    }

    public int getWaveCost() {
        return waveCost;
    }

    public List<ZombieArmorData> getArmors() {
        return armors;
    }

    public String getBehaviorType() {
        return behaviorType;
    }

    public ZombieState getState() {
        return state;
    }

    public boolean isHasParasol() {
        return hasParasol;
    }

    public double getRunningSpeed() {
        return runningSpeed;
    }

    public int getCost() {
        return cost;
    }

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public void setHP(int hp){
        this.HP = hp;
    }

    public void setSpeed(int speed){
        this.runningSpeed = speed;
    }
}
