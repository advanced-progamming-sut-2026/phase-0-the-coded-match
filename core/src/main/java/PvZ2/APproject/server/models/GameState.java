package PvZ2.APproject.server.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    private static final long serialVersionUID = 1L;
    private final String sessionId;
    private final String plantPlayer;
    private final String zombiePlayer;
    private final long startedAt;
    private final long durationMillis;
    private final List<String> actions = new ArrayList<>();
    private boolean finished;
    private String winner;

    public GameState(String sessionId, String plantPlayer, String zombiePlayer, long durationMillis) {
        this.sessionId = sessionId;
        this.plantPlayer = plantPlayer;
        this.zombiePlayer = zombiePlayer;
        this.durationMillis = durationMillis;
        this.startedAt = System.currentTimeMillis();
    }

    public synchronized void addAction(String action) {
        actions.add(action);
    }

    public synchronized boolean isFinished() {
        return finished || System.currentTimeMillis() - startedAt >= durationMillis;
    }

    public synchronized void finish(String winner) {
        finished = true;
        this.winner = winner;
    }

    public synchronized Map<String, String> snapshot() {
        Map<String, String> m = new HashMap<>();
        m.put("sessionId", sessionId);
        m.put("plantPlayer", plantPlayer);
        m.put("zombiePlayer", zombiePlayer);
        m.put("elapsedMillis", String.valueOf(System.currentTimeMillis() - startedAt));
        m.put("remainingMillis", String.valueOf(Math.max(0, durationMillis - (System.currentTimeMillis() - startedAt))));
        m.put("finished", String.valueOf(isFinished()));
        m.put("winner", winner == null ? "" : winner);
        m.put("actions", String.join("\n", actions));
        return m;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPlantPlayer() {
        return plantPlayer;
    }

    public String getZombiePlayer() {
        return zombiePlayer;
    }
}
