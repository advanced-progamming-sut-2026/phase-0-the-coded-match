package models.factories;

import models.strategies.*;

public class ZombieBehaviorFactory {
    public static ZombieBehavior getBehavior(String behaviorName) {
        switch (behaviorName.toLowerCase()) {
            case "normal" :
                return new NormalBehavior();
            case "giant" :
                return new GiantBehavior();
            case "allstar" :
                return new AllStarBehavior();
            case "turquoise" :
                return new TurquoiseBehavior();
            default:
                return null;
        }
    }
}
