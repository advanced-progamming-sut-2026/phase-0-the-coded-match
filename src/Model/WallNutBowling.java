package Model;

import java.util.List;

public class WallNutBowling extends MiniGame{
    private Queue<Plant> conveyorBelt;
    private double redLineCoordinateX;
    private List<RollingNut> activeRollingNuts;
    private int conveyorSpawnCooldownTicks;
    private int currentCooldownTimer;


    public WallNutBowling(int playerSunAmount, boolean isGameOver) {
        super(playerSunAmount, isGameOver);
    }

    @Override
    public void initializeStage() {

    }

    @Override
    public void processInteraction() {

    }

    @Override
    public void checkRules() {

    }

    public void executePlaceNutFromBelt(){

    }


}
