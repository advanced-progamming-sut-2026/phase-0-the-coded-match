package PvZ2.APproject.audio;

import PvZ2.APproject.models.GameSettings;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.MiniGameRelated.Beghouled;
import PvZ2.APproject.models.MiniGameRelated.IZombie;
import PvZ2.APproject.models.MiniGameRelated.VaseBreaker;
import PvZ2.APproject.models.MiniGameRelated.WallNutBowling;
import PvZ2.APproject.models.MiniGameRelated.Zombotany;
import PvZ2.APproject.enums.SeasonType;
import PvZ2.APproject.views.screens.BaseScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

public final class MusicManager {
    private static Music currentMusic;
    private static String currentTrack;

    private MusicManager() {}

    public static void playForScreen(BaseScreen screen) {
        if (screen.getClass().getSimpleName().equals("PlayScreen")) return;
        play("02.");
    }

    public static void playForLevel(Level level) {
        if (level instanceof WallNutBowling) { play("21."); return; }
        if (level instanceof VaseBreaker) { play("27."); return; }
        if (level instanceof IZombie) { play("29."); return; }
        if (level instanceof Zombotany) { play("16."); return; }
        if (level instanceof Beghouled) { play("25."); return; }
        if (level == null || level.getCurrentSeason() == null || level.getCurrentSeason().getType() == null) { play("20."); return; }
        SeasonType type = level.getCurrentSeason().getType();
        switch (type) {
            case ANCIENT_EGYPT -> play("20.");
            case FROSTBITE_CAVES -> play("22.");
            case BIG_WAVE_BEACH -> play("24.");
            case DARK_AGES -> play("26.");
        }
    }

    public static void playFinalWave() {
        play("25.");
    }

    public static void updateVolume() {
        if (currentMusic != null) currentMusic.setVolume(GameSettings.getInstance().getMusicVolume() / 100f);
    }

    public static void dispose() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
        }
        currentTrack = null;
    }

    private static void play(String prefix) {
        if (prefix.equals(currentTrack) && currentMusic != null) {
            updateVolume();
            if (!currentMusic.isPlaying()) currentMusic.play();
            return;
        }
        FileHandle file = findTrack(prefix);
        if (file == null) return;
        dispose();
        currentMusic = Gdx.audio.newMusic(file);
        currentMusic.setLooping(true);
        currentTrack = prefix;
        updateVolume();
        currentMusic.play();
    }

    private static FileHandle findTrack(String prefix) {
        FileHandle directory = Gdx.files.internal("SoundTrack");
        if (!directory.exists()) return null;
        for (FileHandle file : directory.list()) {
            if (file.name().startsWith(prefix) && file.extension().equalsIgnoreCase("mp3")) return file;
        }
        return null;
    }
}
