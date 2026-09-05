package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import pvz.libpvz.pam.PamPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PamActor extends Actor {
    public enum Kind {
        PLANT,
        ZOMBIE
    }

    private static final Map<String, String> PLANT_PATHS = new HashMap<>();
    private static final Map<String, String> ZOMBIE_PATHS = new HashMap<>();
    private static boolean plantsIndexed = false;
    private static boolean zombiesIndexed = false;

    private final PamPlayer player;
    private final String pamPath;
    private final String preferredClip;
    private String clip;
    private float stateTime;
    private float scale = 1f;
    private boolean scaleCalculated;
    private boolean loadRequested;
    private boolean ready;
    public PamActor(Main game, Kind kind, String preferredClip, String... keys) {
        this.player = game.getPlayer();
        this.preferredClip = preferredClip;
        this.pamPath = findPam(kind, keys);
        setTouchable(Touchable.disabled);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (pamPath == null) {
            return;
        }
        if (!loadRequested) {
            loadRequested = true;
            player.loadAsync(pamPath, this::finishLoading);
        }
        if (!ready || clip == null) {
            return;
        }

        if (!scaleCalculated && getWidth() > 0f && getHeight() > 0f) {
            try {
                Rectangle bounds = player.bounds(pamPath, clip);
                if (bounds != null && bounds.width > 0f && bounds.height > 0f) {
                    scale = Math.min(getWidth() / bounds.width, getHeight() / bounds.height) * 0.82f;
                }
            } catch (RuntimeException ignored) {
                scale = 0.5f;
            }
            scaleCalculated = true;
        }

        float x = getX() + getWidth() / 2f;
        float y = getY() + getHeight() / 2f;
        player.draw(batch, pamPath, clip, stateTime, x, y, scale, scale, true);
    }

    private void finishLoading() {
        try {
            List<String> clips = player.clips(pamPath);
            if (clips == null || clips.isEmpty()) {
                return;
            }
            clip = chooseClip(clips);
            ready = clip != null;
        } catch (RuntimeException ignored) {
            ready = false;
        }
    }

    private String chooseClip(List<String> clips) {
        if (preferredClip != null) {
            for (String candidate : clips) {
                if (candidate.equalsIgnoreCase(preferredClip)) {
                    return candidate;
                }
            }
            for (String candidate : clips) {
                if (candidate.toLowerCase(Locale.ROOT).contains(preferredClip.toLowerCase(Locale.ROOT))) {
                    return candidate;
                }
            }
        }
        for (String candidate : clips) {
            if (candidate.toLowerCase(Locale.ROOT).contains("idle")) {
                return candidate;
            }
        }
        for (String candidate : clips) {
            if (candidate.toLowerCase(Locale.ROOT).contains("walk")) {
                return candidate;
            }
        }
        return clips.get(0);
    }

    public static String resolvePlantPam(String... keys) {
        return findPam(Kind.PLANT, keys);
    }

    private static String findPam(Kind kind, String... keys) {
        //System.out.println("NOT FOUND " + kind + " : " + java.util.Arrays.toString(keys));
        Map<String, String> index = kind == Kind.PLANT ? PLANT_PATHS : ZOMBIE_PATHS;
        ensureIndexed(kind);

        for (String key : keys) {
            String normalized = normalize(key);
            if (normalized.equals("PIERCEMINT")) normalized = "SPEARMINT";
            if (normalized.equals("CATTAIL")) normalized = "HOMINGTHISTLE";
            if (normalized.isEmpty()) {
                continue;
            }
            String exact = index.get(normalized);
            if (exact != null) {
                return exact;
            }
        }

        for (String key : keys) {
            String normalized = normalize(key);
            if (normalized.equals("PIERCEMINT")) normalized = "SPEARMINT";
            if (normalized.equals("CATTAIL")) normalized = "HOMINGTHISTLE";
            if (normalized.isEmpty()) {
                continue;
            }
            for (Map.Entry<String, String> entry : index.entrySet()) {
                if (entry.getKey().contains(normalized) || normalized.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private static void ensureIndexed(Kind kind) {
        if (kind == Kind.PLANT && plantsIndexed) {
            return;
        }
        if (kind == Kind.ZOMBIE && zombiesIndexed) {
            return;
        }
        Map<String, String> index = kind == Kind.PLANT ? PLANT_PATHS : ZOMBIE_PATHS;
        String type = kind == Kind.PLANT ? "PLANT" : "ZOMBIE";
        String[] roots;
        if (kind == Kind.PLANT) {
            roots = new String[] {
                "IMAGES/768/INITIAL/PLANT",
                "IMAGES/768/FULL/PLANT",
                "IMAGES/768/INITIAL/EMPOWERMINTS/PLANT"
            };
        } else {
            roots = new String[] {
                "IMAGES/768/INITIAL/ZOMBIE",
                "IMAGES/768/FULL/ZOMBIE"
            };
        }
        for (String rootPath : roots) {
            FileHandle root = Gdx.files.internal(rootPath);
            if (!root.exists()) {
                continue;
            }
            for (FileHandle folder : root.list()) {
                if (!folder.isDirectory()) {
                    continue;
                }
                FileHandle[] files = folder.list(".PAM");

                for (FileHandle pam : files) {
                    String relative = rootPath.replace("IMAGES/", "")
                        + "/" + folder.name()
                        + "/" + pam.name();

                    index.put(normalize(folder.name()), relative);
                    index.put(normalize(pam.nameWithoutExtension()), relative);
                }
            }
        }

        if (kind == Kind.PLANT) {
            plantsIndexed = true;
        } else {
            zombiesIndexed = true;
        }
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
    }
}
