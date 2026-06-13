package models;

public interface ScoreStrategy {

    public int calculatePoints(KillContext context);

    public String getName();


}
