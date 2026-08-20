package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieArmor;
import com.badlogic.gdx.graphics.Color;
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
    private final TextureRegion frozenTexture;
    Map<String, Boolean> armorVisibility = new HashMap<>();

    public ZombieView(Zombie zombie, Main game) {
        this.game = game;
        this.zombie = zombie;
        this.currentState = zombie.getCurrentState();
        this.currentClip = "768/FULL/ZOMBIE/"+zombie.getData().getPath()+"/"+zombie.getData().getPath()+".PAM";
        if(!zombie.getArmors().isEmpty() && zombie.getArmors() != null){
            hasArmor = true;
        }
        frozenTexture = game.getTextures().region("");
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

        setPosition((float) zombie.getX()*80, zombie.getY()*100);
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);
        if (zombie.getIsChilled()) {
            batch.setColor(0.75f, 0.88f, 1f, 1f);
        }else {
            batch.setColor(Color.WHITE);
        }
        String clipName = "walk";
        if (currentState == ZombieState.IDLE) {
            clipName = "idle";
        } else if (currentState == ZombieState.EATING) {
            clipName = "eat";
        }

        batch.setColor(Color.WHITE);

        if (zombie.isFrozenInBlock()) {
            batch.draw(
                frozenTexture,
                getX(),
                getY(),
                80f,
                100f
            );
        }

        game.getPlayer().draw(batch, currentClip, clipName, stateTime, getX(), getY(), true, armorVisibility);
    }
}
