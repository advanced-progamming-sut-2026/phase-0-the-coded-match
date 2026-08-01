package models.factories;

import models.zombies.strategies.*;

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
            case "prospector" :
                return new ProspectorBehavior();
            case "pianist" :
                return new PianistBehavior();
            case "barrelroller" :
                return new BarrelRollerBehavior();
            case "explorer" :
                return new ExplorerBehavior();
            case "dodorider" :
                return new DodoRiderBehavior();
            case "snorkel" :
                return new SnorkelBehavior();
            case "king" :
                return new KingBehavior();
            case "sunproducer" :
                return new SunProducerBehavior();
            default:
                return null;
        }
    }
}
