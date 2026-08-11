package PvZ2.APproject.models.BonusGameRelated;

public interface ScoreStrategy {

    public int calculatePoints(KillContext context);

    public String getName();


}
