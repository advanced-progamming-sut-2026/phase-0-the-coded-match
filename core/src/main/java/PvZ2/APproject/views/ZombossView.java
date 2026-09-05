package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.zombies.Zomboss;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import pvz.libpvz.pam.ClipRef;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ZombossView extends ZombieView {
    private static final String RETICLE = "768/INITIAL/EFFECTS/MISSILE_TOE_RETICLE/MISSILE_TOE_RETICLE.PAM";
    private static final String EGYPT_EXPLOSION = "768/INITIAL/EFFECTS/ZOMBOSS_MISSILE_EXPLOSION_EGYPT/ZOMBOSS_MISSILE_EXPLOSION_EGYPT.PAM";
    private static final String DARK_FIREBALL = "768/FULL/EFFECTS/ZOMBOSS_DARK_FIREBALL/ZOMBOSS_DARK_FIREBALL.PAM";
    private static final String DARK_EXPLOSION = "768/FULL/EFFECTS/ZOMBOSS_MISSILE_EXPLOSION_DARK/ZOMBOSS_MISSILE_EXPLOSION_DARK.PAM";
    private static final String ICE_EXPLOSION = "768/FULL/EFFECTS/ZOMBOSS_MISSILE_EXPLOSION_ICEAGE/ZOMBOSS_MISSILE_EXPLOSION_ICEAGE.PAM";
    private static final String ICE_BLOCK = "768/FULL/EFFECTS/FROSTBITE_ICE_BLOCK_PLANT/FROSTBITE_ICE_BLOCK_PLANT.PAM";
    private static final String FROST_WIND = "768/FULL/EFFECTS/FROSTBITE_CHILL_WIND/FROSTBITE_CHILL_WIND.PAM";
    private static final String SHARK = "768/FULL/EFFECTS/ZOMBOSS_SHARK_PROJECTILE/ZOMBOSS_SHARK_PROJECTILE.PAM";
    private static final String TURBINE = "768/FULL/EFFECTS/ZOMBOSS_TURBINE_WIND/ZOMBOSS_TURBINE_WIND.PAM";
    private static final String SCORCHED = "768/FULL/EFFECTS/SCORCHED_EARTH_TILE/SCORCHED_EARTH_TILE.PAM";

    private final Main game;
    private final Zomboss zomboss;
    private final String pamPath;
    private final Map<String, List<String>> effectClips = new HashMap<>();
    private List<String> clips;
    private Rectangle anchorBounds;
    private float time;
    private float renderScale = 0.55f;
    private Zomboss.Action lastAction;
    private int lastHealth;
    private float damageFlash;

    public ZombossView(Zomboss zomboss, Main game) {
        super(zomboss, game);
        this.game = game;
        this.zomboss = zomboss;
        this.lastHealth = zomboss.getCurrentHp();
        this.pamPath = resolvePath();
        if (pamPath != null) {
            try {
                game.getPlayer().loadSync(pamPath);
                clips = game.getPlayer().clips(pamPath);
                renderScale = resolveStableScale();
            } catch (RuntimeException ignored) {
                clips = null;
            }
        }
    }

    private String resolvePath() {
        String path = zomboss.getData().getPath();
        if (path == null || path.isBlank()) {
            String id = zomboss.getData().getId();
            if (id == null || id.isBlank()) return null;
            path = id.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toUpperCase(Locale.ROOT);
        }
        String full = "768/FULL/ZOMBIE/" + path + "/" + path + ".PAM";
        String initial = "768/INITIAL/ZOMBIE/" + path + "/" + path + ".PAM";
        if (Gdx.files.internal("IMAGES/" + full).exists()) return full;
        if (Gdx.files.internal("IMAGES/" + initial).exists()) return initial;
        return null;
    }

    private String findClip(String... words) {
        return findClip(clips, words);
    }

    private String findClip(List<String> available, String... words) {
        if (available == null || available.isEmpty()) return null;
        for (String word : words) {
            for (String clip : available) {
                if (clip.equalsIgnoreCase(word)) return clip;
            }
            String lower = word.toLowerCase(Locale.ROOT);
            for (String clip : available) {
                if (clip.toLowerCase(Locale.ROOT).contains(lower)) return clip;
            }
        }
        return available.get(0);
    }

    private float resolveStableScale() {
        String reference = findClip("idle", "walk");
        if (reference == null) return 0.55f;
        try {
            Rectangle bounds = game.getPlayer().bounds(pamPath, reference);
            if (bounds == null || bounds.width <= 0f || bounds.height <= 0f) return 0.55f;
            anchorBounds = new Rectangle(bounds);
            float widthScale = PlayScreen.TILE_WIDTH * 3.0f / bounds.width;
            float heightScale = PlayScreen.TILE_HEIGHT * 3.6f / bounds.height;
            return Math.max(0.18f, Math.min(0.76f, Math.min(widthScale, heightScale)));
        } catch (RuntimeException ignored) {
            return 0.55f;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        int health = zomboss.getCurrentHp();
        if (health < lastHealth) damageFlash = 0.14f;
        lastHealth = health;
        if (damageFlash > 0f) damageFlash = Math.max(0f, damageFlash - delta);
        if (lastAction != zomboss.getAction()) {
            lastAction = zomboss.getAction();
            time = 0f;
        } else {
            time += delta;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (pamPath == null || clips == null || clips.isEmpty()) {
            super.draw(batch, parentAlpha);
            return;
        }
        drawBoss(batch, parentAlpha);
        drawBossEffects(batch);
        batch.setColor(Color.WHITE);
    }

    private void drawBoss(Batch batch, float parentAlpha) {
        String clip = switch (zomboss.getAction()) {
            case STUNNED -> findClip("stun", "damage", "hit", "idle");
            case SUMMON -> findClip("summon", "spawn", "attack", "idle");
            case MOVE, EGYPT_RETREAT -> findClip("walk", "move", "idle");
            case EGYPT_CHARGE -> findClip("charge", "attack", "walk", "move");
            case EGYPT_MISSILE, DARK_FIREBALL, FROST_MISSILE, BEACH_SHARK -> findClip("shoot", "launch", "attack", "idle");
            case DARK_BREATH, FROST_WIND, FROST_COLUMN, BEACH_TURBINE -> findClip("attack", "action", "idle");
            case IDLE -> findClip("idle", "walk");
        };
        if (clip == null) return;
        try {
            float rightX = getX() + getWidth() * 0.88f;
            float groundY = getY() + getHeight() * 0.06f - PlayScreen.TILE_HEIGHT * 0.45f;
            float drawX = rightX;
            float drawY = groundY;
            if (anchorBounds != null) {
                drawX -= (anchorBounds.x + anchorBounds.width) * renderScale;
                drawY -= anchorBounds.y * renderScale;
            }
            float alpha = getColor().a * parentAlpha;
            if (damageFlash > 0f) batch.setColor(1f, 0.2f, 0.2f, alpha);
            else if (zomboss.isStunned()) batch.setColor(0.72f, 0.86f, 1f, alpha);
            else batch.setColor(1f, 1f, 1f, alpha);
            ClipRef ref = game.getPlayer().getClip(pamPath, clip);
            game.getPlayer().draw(batch, ref, time, drawX, drawY, renderScale, renderScale, true, Collections.emptyMap());
        } catch (RuntimeException ignored) {
        }
        batch.setColor(Color.WHITE);
    }

    private void drawBossEffects(Batch batch) {
        switch (zomboss.getAction()) {
            case EGYPT_MISSILE -> drawMissileEffect(batch, EGYPT_EXPLOSION);
            case DARK_FIREBALL -> drawDarkFireballs(batch);
            case DARK_BREATH -> drawDarkBreath(batch);
            case FROST_MISSILE -> drawMissileEffect(batch, ICE_EXPLOSION);
            case FROST_WIND -> drawFrostWind(batch);
            case FROST_COLUMN -> drawFrozenColumn(batch);
            case BEACH_SHARK -> drawShark(batch);
            case BEACH_TURBINE -> drawTurbine(batch);
            default -> {
            }
        }
    }

    private void drawMissileEffect(Batch batch, String explosion) {
        if (zomboss.getTargetCells().isEmpty()) return;
        Zomboss.TargetCell target = zomboss.getTargetCells().get(0);
        if (!zomboss.isActionResolved()) drawEffectAtCell(batch, RETICLE, target, 0.9f);
        else if (zomboss.getEffectTimer() > 0f) drawEffectAtCell(batch, explosion, target, 1.15f);
    }

    private void drawDarkFireballs(Batch batch) {
        if (zomboss.getTargetCells().isEmpty()) return;
        if (!zomboss.isActionResolved()) {
            float progress = Math.min(1f, zomboss.getActionProgress() / 0.75f);
            float startX = getX() + getWidth() * 0.35f;
            float startY = getY() + getHeight() * 0.9f;
            for (Zomboss.TargetCell target : zomboss.getTargetCells()) {
                float tx = cellCenterX(target.getColumn());
                float ty = cellCenterY(target.getRow());
                drawEffectAt(batch, DARK_FIREBALL, lerp(startX, tx, progress), lerp(startY, ty, progress), PlayScreen.TILE_WIDTH * 0.8f, PlayScreen.TILE_HEIGHT * 0.8f);
            }
        } else if (zomboss.getEffectTimer() > 0f) {
            drawResolvedCells(batch, DARK_EXPLOSION, 1.05f);
        }
    }

    private void drawFrostWind(Batch batch) {
        int rowA = zomboss.getTargetRowA();
        int rowB = zomboss.getTargetRowB();
        if (rowA <= 0 || rowB <= 0) return;
        int columns = 9;
        if (GameManagerController.getInstance().getCurrentLevel() != null) {
            columns = Math.max(1, GameManagerController.getInstance().getCurrentLevel().getGameMap().getColumns());
        }
        float centerX = PlayScreen.BOARD_X + PlayScreen.TILE_WIDTH * columns * 0.5f;
        float width = PlayScreen.TILE_WIDTH * columns;
        drawEffectAt(batch, FROST_WIND, centerX, cellCenterY(rowA), width, PlayScreen.TILE_HEIGHT * 0.95f);
        drawEffectAt(batch, FROST_WIND, centerX, cellCenterY(rowB), width, PlayScreen.TILE_HEIGHT * 0.95f);
    }

    private void drawFrozenColumn(Batch batch) {
        if (!zomboss.isActionResolved() || zomboss.getEffectTimer() <= 0f) return;
        for (Zomboss.TargetCell target : zomboss.getTargetCells()) drawEffectAtCell(batch, ICE_BLOCK, target, 0.72f);
    }

    private void drawShark(Batch batch) {
        if (zomboss.getTargetCells().isEmpty() || zomboss.isActionResolved()) return;
        float progress = Math.min(1f, zomboss.getActionProgress() / 0.78f);
        float startX = getX() + getWidth() * 0.25f;
        float startY = getY() + getHeight() * 0.75f;
        int index = 0;
        for (Zomboss.TargetCell target : zomboss.getTargetCells()) {
            float offset = (index++ - (zomboss.getTargetCells().size() - 1) * 0.5f) * PlayScreen.TILE_HEIGHT * 0.22f;
            drawEffectAt(batch, SHARK,
                lerp(startX, cellCenterX(target.getColumn()), progress),
                lerp(startY + offset, cellCenterY(target.getRow()), progress),
                PlayScreen.TILE_WIDTH * 1.15f,
                PlayScreen.TILE_HEIGHT * 1.0f);
        }
    }

    private void drawDarkBreath(Batch batch) {
        int rowA = zomboss.getTargetRowA();
        int rowB = zomboss.getTargetRowB();
        if (rowA <= 0 || rowB <= 0) return;
        int columns = 9;
        if (GameManagerController.getInstance().getCurrentLevel() != null) {
            columns = Math.max(1, GameManagerController.getInstance().getCurrentLevel().getGameMap().getColumns());
        }
        float progress = Math.min(1f, zomboss.getActionProgress() / 0.7f);
        int count = Math.max(1, Math.min(columns, (int) Math.ceil(columns * progress)));
        for (int i = 0; i < count; i++) {
            int column = columns - i;
            drawEffectAt(batch, SCORCHED, cellCenterX(column), cellCenterY(rowA), PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
            drawEffectAt(batch, SCORCHED, cellCenterX(column), cellCenterY(rowB), PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
        }
    }

    private void drawTurbine(Batch batch) {
        int rowA = zomboss.getTargetRowA();
        int rowB = zomboss.getTargetRowB();
        if (rowA <= 0 || rowB <= 0) return;
        float centerX = PlayScreen.BOARD_X + PlayScreen.TILE_WIDTH * 6.0f;
        float centerY = (cellCenterY(rowA) + cellCenterY(rowB)) * 0.5f;
        drawEffectAt(batch, TURBINE, centerX, centerY, PlayScreen.TILE_WIDTH * 5.2f, PlayScreen.TILE_HEIGHT * 2.2f);
    }

    private void drawResolvedCells(Batch batch, String path, float size) {
        for (Zomboss.TargetCell target : zomboss.getTargetCells()) drawEffectAtCell(batch, path, target, size);
    }

    private void drawEffectAtCell(Batch batch, String path, Zomboss.TargetCell target, float tileScale) {
        drawEffectAt(batch, path, cellCenterX(target.getColumn()), cellCenterY(target.getRow()),
            PlayScreen.TILE_WIDTH * tileScale, PlayScreen.TILE_HEIGHT * tileScale);
    }

    private void drawEffectAt(Batch batch, String path, float centerX, float centerY, float maxWidth, float maxHeight) {
        try {
            if (!Gdx.files.internal("IMAGES/" + path).exists()) return;
            List<String> available = effectClips.get(path);
            if (available == null) {
                game.getPlayer().loadSync(path);
                available = game.getPlayer().clips(path);
                effectClips.put(path, available);
            }
            String clip = findClip(available, "animation", "idle", "loop", "attack", "fire", "water");
            if (clip == null) return;
            Rectangle bounds = game.getPlayer().bounds(path, clip);
            float scale = 0.5f;
            float drawX = centerX;
            float drawY = centerY;
            if (bounds != null && bounds.width > 0f && bounds.height > 0f) {
                scale = Math.min(maxWidth / bounds.width, maxHeight / bounds.height);
                drawX -= (bounds.x + bounds.width * 0.5f) * scale;
                drawY -= (bounds.y + bounds.height * 0.5f) * scale;
            }
            game.getPlayer().draw(batch, path, clip, time, drawX, drawY, scale, scale, true);
        } catch (RuntimeException ignored) {
        }
    }

    private float cellCenterX(int column) {
        return PlayScreen.BOARD_X + (column - 0.5f) * PlayScreen.TILE_WIDTH;
    }

    private float cellCenterY(int row) {
        return PlayScreen.BOARD_Y + (row - 0.5f) * PlayScreen.TILE_HEIGHT;
    }

    private float lerp(float start, float end, float amount) {
        return start + (end - start) * Math.max(0f, Math.min(1f, amount));
    }
}
