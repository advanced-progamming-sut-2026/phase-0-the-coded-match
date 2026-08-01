package models.factories;

import models.zombies.strategies.AllStarBehavior;
import models.zombies.strategies.ArcadeBehavior;
import models.zombies.strategies.BarrelRollerBehavior;
import models.zombies.strategies.DodoRiderBehavior;
import models.zombies.strategies.ExplorerBehavior;
import models.zombies.strategies.FishermanBehavior;
import models.zombies.strategies.GiantBehavior;
import models.zombies.strategies.HunterBehavior;
import models.zombies.strategies.JugglerBehavior;
import models.zombies.strategies.KingBehavior;
import models.zombies.strategies.NormalBehavior;
import models.zombies.strategies.OctopusBehavior;
import models.zombies.strategies.PianistBehavior;
import models.zombies.strategies.ProspectorBehavior;
import models.zombies.strategies.SnorkelBehavior;
import models.zombies.strategies.SunProducerBehavior;
import models.zombies.strategies.TroglobiteBehavior;
import models.zombies.strategies.TurquoiseBehavior;
import models.zombies.strategies.WizardBehavior;
import models.zombies.strategies.ZombieBehavior;

public class ZombieBehaviorFactory {
    public static ZombieBehavior getBehavior(String behaviorName) {
        String normalized = behaviorName == null ? "normal" : behaviorName.trim().toLowerCase();
        return switch (normalized) {
            case "giant" -> new GiantBehavior();
            case "allstar" -> new AllStarBehavior();
            case "turquoise" -> new TurquoiseBehavior();
            case "prospector" -> new ProspectorBehavior();
            case "pianist" -> new PianistBehavior();
            case "barrelroller" -> new BarrelRollerBehavior();
            case "arcade" -> new ArcadeBehavior();
            case "troglobite" -> new TroglobiteBehavior();
            case "explorer" -> new ExplorerBehavior();
            case "dodorider" -> new DodoRiderBehavior();
            case "snorkel" -> new SnorkelBehavior();
            case "fisherman" -> new FishermanBehavior();
            case "juggler" -> new JugglerBehavior();
            case "wizard" -> new WizardBehavior();
            case "hunter" -> new HunterBehavior();
            case "octopus" -> new OctopusBehavior();
            case "king" -> new KingBehavior();
            case "sunproducer" -> new SunProducerBehavior();
            default -> new NormalBehavior();
        };
    }
}
