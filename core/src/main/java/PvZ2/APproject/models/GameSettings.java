package PvZ2.APproject.models;

public class GameSettings {
    private static GameSettings instance;
    private int gameSpeed = 1;
    private boolean showGrid = false;
    private boolean debugMode = false;
    private int musicVolume = 100;
    private int soundEffectsVolume = 100;

    private GameSettings() {}

    public static GameSettings getInstance() {
        if (instance == null) instance = new GameSettings();
        return instance;
    }

    public int getGameSpeed() { return gameSpeed; }
    public void setGameSpeed(int gameSpeed) { this.gameSpeed = gameSpeed; }

    public boolean isShowGrid() { return showGrid; }
    public void setShowGrid(boolean showGrid) { this.showGrid = showGrid; }

    public boolean isDebugMode() { return debugMode; }
    public void setDebugMode(boolean debugMode) { this.debugMode = debugMode; }

    public int getMusicVolume() { return musicVolume; }
    public void setMusicVolume(int musicVolume) { this.musicVolume = Math.max(0, Math.min(100, musicVolume)); }

    public int getSoundEffectsVolume() { return soundEffectsVolume; }
    public void setSoundEffectsVolume(int soundEffectsVolume) { this.soundEffectsVolume =
        Math.max(0, Math.min(100, soundEffectsVolume)); }

    public int getGameDifficulty(){return App.getCurrentUser().getDifficultyLevel();}
    public String setGameDifficulty(int difficulty){
        if(App.getCurrentUser().getDifficultyLevel() == difficulty){
            return "Difficulty is already set";
        }
        App.getCurrentUser().setDifficultyLevel(difficulty);
        return "Difficulty changed to " + difficulty;
    }

}
