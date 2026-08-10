package models.zombies;

public class ZombieSpawnData {
    private String zombieAlias;
    private int lane;
    private int count;
    private int delayTicks;

    public String getZombieAlias() {
        return zombieAlias;
    }

    public int getLane() {
        return lane;
    }

    public int getCount() {
        return count;
    }

    public int getDelayTicks() {
        return delayTicks;
    }
}