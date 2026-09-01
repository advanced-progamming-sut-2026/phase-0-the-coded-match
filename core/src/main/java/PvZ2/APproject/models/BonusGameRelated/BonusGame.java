package PvZ2.APproject.models.BonusGameRelated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;

public class BonusGame {
    private int totalMioPoints;
    private long dailyGameZombies;
    private List<ScoreStrategy> activeCases;
    private final List<String> dailyZombieOrder;
    private int nextZombieIndex;
    private final Random dailyRandom;

    public BonusGame() {
        dailyGameZombies = LocalDate.now().toEpochDay();
        dailyRandom = new Random(dailyGameZombies);
        activeCases = new ArrayList<>();
        activeCases.add(strategy("multi kill", c -> Math.max(0, c.zombiesKilledByOneShot - 1) * 100));
        activeCases.add(strategy("quick kill", c -> c.timeSinceLastKill <= 30 ? 75 : 0));
        activeCases.add(strategy("high damage", c -> Math.max(0, c.damageDealt / 100) * 10));
        activeCases.add(strategy("clean sweep", c -> c.allZombiesDead ? 500 : 0));
        activeCases.add(strategy("single shot", c -> c.zombiesKilledByOneShot > 0 ? 25 : 0));
        dailyZombieOrder = createDailyZombieOrder();
    }

    private ScoreStrategy strategy(String name, PointRule rule) {
        return new ScoreStrategy() {
            public int calculatePoints(KillContext context) { return rule.points(context); }
            public String getName() { return name; }
        };
    }

    public int score(KillContext context) {
        if (context == null) return 0;
        int gained = 0;
        for (ScoreStrategy strategy : activeCases) gained += Math.max(0, strategy.calculatePoints(context));
        totalMioPoints += gained;
        return gained;
    }

    private List<String> createDailyZombieOrder() {
        List<ZombieData> zombies = ZombieRepository.getInstance().getAllZombies();
        List<String> order = new ArrayList<>();
        if (zombies == null || zombies.isEmpty()) return order;
        int count = Math.max(20, zombies.size());
        for (int i = 0; i < count; i++) {
            ZombieData zombie = zombies.get(dailyRandom.nextInt(zombies.size()));
            order.add(zombie.getDisplayName());
        }
        return order;
    }

    public String nextZombie() {
        if (nextZombieIndex >= dailyZombieOrder.size()) return null;
        String zombie = dailyZombieOrder.get(nextZombieIndex);
        nextZombieIndex++;
        return zombie;
    }

    public int nextLane(int rows) { return 1 + dailyRandom.nextInt(Math.max(1, rows)); }
    public boolean hasRemainingZombies() { return nextZombieIndex < dailyZombieOrder.size(); }
    public Random createDailyRandom() { return new Random(dailyGameZombies); }

    public void addExternalPoints(int points) { totalMioPoints += Math.max(0, points); }
    public int getTotalMioPoints() { return totalMioPoints; }
    public long getDailyGameZombies() { return dailyGameZombies; }
    public List<ScoreStrategy> getActiveCases() { return Collections.unmodifiableList(activeCases); }
    public List<String> getDailyZombieOrder() { return Collections.unmodifiableList(dailyZombieOrder); }
    private interface PointRule { int points(KillContext context); }
}
