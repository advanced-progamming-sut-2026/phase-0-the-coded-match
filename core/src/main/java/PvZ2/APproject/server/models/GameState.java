package PvZ2.APproject.server.models;

import java.util.*;

public class GameState {
    private static final long serialVersionUID = 2L;
    private final String sessionId;
    private final String plantPlayer;
    private final String zombiePlayer;
    private final long startedAt;
    private final long durationMillis;
    private final int stage;
    private final List<GameAction> actions = new ArrayList<>();
    private final Map<String, String> entities = new LinkedHashMap<>();
    private boolean finished;
    private String winner;

    public GameState(String sessionId, String plantPlayer, String zombiePlayer, int stage, long durationMillis) {
        this.sessionId = sessionId;
        this.plantPlayer = plantPlayer;
        this.zombiePlayer = zombiePlayer;
        this.stage = stage;
        this.durationMillis = durationMillis;
        this.startedAt = System.currentTimeMillis();
    }

    public synchronized void addAction(GameAction action) {
        actions.add(action);
        String entityId = action.getEntityId();
        if (entityId != null && !entityId.isBlank()) {
            if ("REMOVE_ENTITY".equalsIgnoreCase(action.getAction())) entities.remove(entityId);
            else entities.put(entityId, action.getEntityType() + "@" + action.getX() + "," + action.getY());
        }
    }

    public synchronized boolean isFinished() {
        return finished || System.currentTimeMillis() - startedAt >= durationMillis;
    }

    public synchronized void finish(String winner) {
        finished = true;
        this.winner = winner;
    }

    public synchronized Map<String, String> snapshot() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("sessionId", sessionId);
        m.put("plantPlayer", plantPlayer);
        m.put("zombiePlayer", zombiePlayer);
        m.put("stage", String.valueOf(stage));
        m.put("elapsedMillis", String.valueOf(System.currentTimeMillis() - startedAt));
        m.put("remainingMillis", String.valueOf(Math.max(0, durationMillis - (System.currentTimeMillis() - startedAt))));
        m.put("finished", String.valueOf(isFinished()));
        m.put("winner", winner == null ? "" : winner);
        m.put("actions", encodeActions());
        m.put("entities", encodeEntities());
        return m;
    }

    private String encodeActions() {
        StringBuilder b = new StringBuilder();
        for (GameAction a : actions) {
            b.append(escape(a.getPlayer())).append('|')
                .append(a.getRole()).append('|')
                .append(escape(a.getAction())).append('|')
                .append(escape(a.getEntityId() == null ? "" : a.getEntityId())).append('|')
                .append(escape(a.getEntityType() == null ? "" : a.getEntityType())).append('|')
                .append(a.getX()).append('|').append(a.getY()).append('|').append(a.getTimestamp()).append('\n');
        }
        return b.toString();
    }

    private String encodeEntities() {
        StringBuilder b = new StringBuilder();
        entities.forEach((id, value) -> b.append(escape(id)).append('=').append(escape(value)).append('\n'));
        return b.toString();
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("|", "\\|").replace("\n", "\\n").replace("=", "\\=");
    }

    public String getSessionId() { return sessionId; }
    public long getStartedAt() { return startedAt; }
    public long getDurationMillis() { return durationMillis; }
    public String getWinner() { return winner; }
    public String getPlantPlayer() { return plantPlayer; }
    public String getZombiePlayer() { return zombiePlayer; }
    public int getStage() { return stage; }
}
