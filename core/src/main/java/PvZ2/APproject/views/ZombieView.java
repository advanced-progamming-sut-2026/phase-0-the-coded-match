package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieArmor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import pvz.libpvz.pam.PamPlayer;

import java.util.HashMap;
import java.util.Map;

public class ZombieView extends Actor {
    private final Main game;
    private final Zombie zombie;
    private ZombieState currentState;
    private String currentClip;
    private float stateTime = 0f;
    private Boolean hasArmor = false;
    Map<String, Boolean> armorVisibility = new HashMap<>();

    public ZombieView(Zombie zombie, Main game) {
        this.game = game;
        this.zombie = zombie;
        this.currentState = zombie.getCurrentState();
        this.currentClip = "768/FULL/ZOMBIE/"+zombie.getData().getPath()+"/"+zombie.getData().getPath()+".PAM";
        if(!zombie.getArmors().isEmpty() && zombie.getArmors() != null){
            hasArmor = true;
        }
    }

    @Override
    public void act(float delta){
        super.act(delta);
        stateTime += delta;

        if (zombie.getCurrentState() != currentState) {
            currentState = zombie.getCurrentState();
            stateTime = 0f;
        }

        if (hasArmor) {
            for (ZombieArmor armor : zombie.getArmors()) {
                armorVisibility.put(armor.getData().getPath(), armor.getCurrentHp() > 0);
            }
        }

        setPosition((float) zombie.getX()*80, zombie.getY()*100); // this has to get multiplied by cell width and height
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);
        String clipName = "walk";
        if (currentState == ZombieState.IDLE) {
            clipName = "idle";
        } else if (currentState == ZombieState.EATING) {
            clipName = "eat";
        }

        game.getPlayer().draw(batch, currentClip, clipName, stateTime, getX(), getY(), true, armorVisibility);
    }
}
