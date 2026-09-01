package PvZ2.APproject.server.models;

public class GameAction {
    private static final long serialVersionUID = 1L;
    private final String player;
    private final PlayerRole role;
    private final String action;
    private final String entityId;
    private final String entityType;
    private final double x;
    private final double y;
    private final long timestamp;

    public GameAction(String player, PlayerRole role, String action, String entityId,
                      String entityType, double x, double y) {
        this.player = player;
        this.role = role;
        this.action = action;
        this.entityId = entityId;
        this.entityType = entityType;
        this.x = x;
        this.y = y;
        this.timestamp = System.currentTimeMillis();
    }

    public String getPlayer() { return player; }
    public PlayerRole getRole() { return role; }
    public String getAction() { return action; }
    public String getEntityId() { return entityId; }
    public String getEntityType() { return entityType; }
    public double getX() { return x; }
    public double getY() { return y; }
    public long getTimestamp() { return timestamp; }
}
