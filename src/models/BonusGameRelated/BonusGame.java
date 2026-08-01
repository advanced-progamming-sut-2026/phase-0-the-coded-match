package models.BonusGameRelated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BonusGame {
    private int totalMioPoints;
    private final long dailyGameZombies;
    private final List<ScoreStrategy> activeCases;

    public BonusGame() {
        dailyGameZombies = LocalDate.now().toEpochDay();
        activeCases = new ArrayList<>();
        activeCases.add(new ScoreStrategy() {
            @Override
            public int calculatePoints(KillContext context) {
                return Math.max(0, context.zombiesKilledByOneShot - 1) * 100;
            }

            @Override
            public String getName() {
                return "multi kill";
            }
        });
        activeCases.add(new ScoreStrategy() {
            @Override
            public int calculatePoints(KillContext context) {
                return context.timeSinceLastKill <= 3000 ? 75 : 0;
            }

            @Override
            public String getName() {
                return "quick kill";
            }
        });
        activeCases.add(new ScoreStrategy() {
            @Override
            public int calculatePoints(KillContext context) {
                return Math.max(0, context.damageDealt / 50);
            }

            @Override
            public String getName() {
                return "high damage";
            }
        });
        activeCases.add(new ScoreStrategy() {
            @Override
            public int calculatePoints(KillContext context) {
                return context.allZombiesDead ? 250 : 0;
            }

            @Override
            public String getName() {
                return "wave clear";
            }
        });
        activeCases.add(new ScoreStrategy() {
            @Override
            public int calculatePoints(KillContext context) {
                return context.zombiesKilledByOneShot >= 3 && context.timeSinceLastKill <= 3000 ? 200 : 0;
            }

            @Override
            public String getName() {
                return "combo";
            }
        });
    }

    public int score(KillContext context) {
        int gained = 0;
        for (ScoreStrategy strategy : activeCases) {
            gained += Math.max(0, strategy.calculatePoints(context));
        }
        totalMioPoints += gained;
        return gained;
    }

    public int getTotalMioPoints() {
        return totalMioPoints;
    }

    public long getDailyGameZombies() {
        return dailyGameZombies;
    }

    public List<ScoreStrategy> getActiveCases() {
        return activeCases;
    }
}
