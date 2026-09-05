package PvZ2.APproject.views.actors;

import PvZ2.APproject.Main;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.seasons.EnvironmentEvent;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import pvz.libpvz.textures.TextureBank;

public class EnvironmentView extends Actor {
    private final Main game;
    private final Level level;
    private final TextureBank textures;
    private EnvironmentEvent currentEvent;
    private float stateTime;


    public EnvironmentView(Main game, Level level, TextureBank textures) {
        this.game = game;
        this.level = level;
        this.textures = textures;
        this.currentEvent = null;
        setTouchable(Touchable.disabled);
    }

    public void play(EnvironmentEvent event) {
        currentEvent = event;
        stateTime = 0f;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (currentEvent == null) {
            EnvironmentEvent newEvent = level.consumePendingEnvironmentEvent();
            if (newEvent != null) {
                play(newEvent);
            }
        }
        if (currentEvent == null) {
            return;
        }

        stateTime += delta;

        if (stateTime >= currentEvent.getDuration()) {
            currentEvent = null;
            stateTime = 0f;
        }

    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (currentEvent == null) {
            return;
        }

        switch (currentEvent.getType()) {
            case SANDSTORM:
                drawSandstorm(batch);
                break;

            case ICE_WIND:
                drawIceWind(batch);
                break;

            case TIDE:
                drawTide(batch);
                break;
        }
    }

    private void drawSandstorm(Batch batch){
        batch.setColor(1f, 1f, 1f, 0.5f);
        game.getPlayer().draw(batch, "768/INITIAL/EFFECTS/SANDSTORM_TOP/SANDSTORM_TOP.PAM", "loop", stateTime, 1185f, 470f, 2.0f, 2.8f, true);
        batch.setColor(Color.WHITE);
    }

    private void drawIceWind(Batch batch){
        game.getPlayer().draw(batch, "768/FULL/EFFECTS/FROSTBITE_CHILL_WIND/FROSTBITE_CHILL_WIND.PAM", "animation", stateTime, 1150f, 470f, 2.0f, 2.8f, true);
    }

    private void drawTide(Batch batch){
        game.getPlayer().draw(batch, "768/FULL/BACKGROUNDS/WAVE_UPPERLAYER/WAVE_UPPERLAYER.PAM", "water", stateTime, 1185f, 323f, 0.5f, 0.4f,true);
    }
}
