package PvZ2.APproject.views;

import PvZ2.APproject.models.zombies.Zombie;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ZombieView extends Actor {
    private final Zombie zombie;
    private float stateTime;
    private TextureRegion currentFrame;

    public ZombieView(Zombie zombie) {
        this.zombie = zombie;
    }
}
