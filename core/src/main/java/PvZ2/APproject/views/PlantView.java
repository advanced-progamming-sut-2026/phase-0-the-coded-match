package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.enums.PlantState;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.views.screens.PamActor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.Collections;

public class PlantView extends Actor {
    private final Main game;
    private final Plant plant;
    private PlantState state;
    private float stateTime;
    private final String clip;

    public PlantView(Plant plant, Main game) {
        this.plant = plant;
        this.game = game;
        this.state = plant.getCurrentState();
        this.clip = PamActor.resolvePlantPam(
                plant.getData().getId(),
                plant.getData().getName(),
                plant.getData().getDisplayName()
        );
    }

    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        if (state != plant.getCurrentState()) {
            state = plant.getCurrentState();
            stateTime = 0;
        }
        setPosition(plant.getX() * 80f, plant.getY() * 100f);
    }

    public void draw(Batch batch, float parentAlpha) {
        String clipName = "idle";
        if (state == PlantState.SHOOTING || state == PlantState.ATTACKING) clipName = "attack";
        if (state == PlantState.EXPLODING) clipName = "explode";
        if (state == PlantState.DEATH) clipName = "death";
        if (state == PlantState.PRODUCING) clipName = "produce";
        game.getPlayer().draw(batch, clip, clipName, stateTime, getX(), getY(), true, Collections.emptyMap());
    }
}
