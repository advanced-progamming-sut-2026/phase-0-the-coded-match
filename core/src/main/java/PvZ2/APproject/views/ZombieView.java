package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieArmor;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
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
    private final String frozenClip;
    Map<String, Boolean> armorVisibility = new HashMap<>();

    public ZombieView(Zombie zombie, Main game) {
        this.game = game;
        this.zombie = zombie;
        this.currentState = zombie.getCurrentState();
        this.currentClip = getCurrentClip();
        if(!zombie.getArmors().isEmpty() && zombie.getArmors() != null){
            hasArmor = true;
        }
        frozenClip = "768/FULL/EFFECTS/FROSTBITE_ICE_BLOCK_ZOMBIE/FROSTBITE_ICE_BLOCK_ZOMBIE.PAM";
    }

    public String getCurrentClip() {
        String zombiePath = zombie.getData().getPath();

        String fullPath = "768/FULL/ZOMBIE/" + zombiePath + "/" + zombiePath + ".PAM";
        String initialPath = "768/INITIAL/ZOMBIE/" + zombiePath + "/" + zombiePath + ".PAM";

        if (Gdx.files.internal("./IMAGES/" + fullPath).exists()) {
            return fullPath;
        } else if (Gdx.files.internal("./IMAGES/" + initialPath).exists()){
            return initialPath;
        }

        return null;
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

        setPosition(PlayScreen.BOARD_X + (float) zombie.getX()*PlayScreen.TILE_WIDTH, PlayScreen.BOARD_Y+ zombie.getY()*PlayScreen.TILE_HEIGHT);
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
            game.getPlayer().draw(batch, frozenClip, "idle", stateTime, getX(), getY(), true);
//            batch.draw(
//                frozenTexture,
//                getX(),
//                getY(),
//                80f,
//                100f
//            ); TODO: SEE IF IT WORKS IF IT DOESNT SWITCH TO AN ICE IMAGE NOT AN ANIMATION!!
        }

        game.getPlayer().draw(batch, currentClip, clipName, stateTime, getX(), getY(), true, armorVisibility);
    }
}
