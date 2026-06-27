package models.factories;

import models.strategies.GiantBehavior;
import models.strategies.NormalBehavior;
import models.strategies.ZombieBehavior;

public class ZombieBehaviorFactory {
    public static ZombieBehavior getBehavior(String behaviorName) {
        switch (behaviorName.toLowerCase()) {
            case "normal" :
                return new NormalBehavior();
            case "giant" :
                return new GiantBehavior();
            case "" :

            default:
                return null;
        }
    }
}
