package PvZ2.APproject.models.BonusGameRelated;

public interface ScoreStrategy {
    int calculatePoints(KillContext context);
    String getName();
}
