package PvZ2.APproject.server.models;

import PvZ2.APproject.server.ClientHandler;

import java.util.UUID;

public final class GameSession {
    private final String id = UUID.randomUUID().toString();
    private final String playerA;
    private final String playerB;
    private final int stage;
    private final GameState state;
    private final ClientHandler handlerA;
    private final ClientHandler handlerB;

    public GameSession(String a, String b, int stage, ClientHandler ha, ClientHandler hb) {
        playerA = a;
        playerB = b;
        this.stage = Math.max(1, Math.min(3, stage));
        handlerA = ha;
        handlerB = hb;
        state = new GameState(id, a, b, this.stage, 120_000);
    }

    public String getId() { return id; }
    public String getPlayerA() { return playerA; }
    public String getPlayerB() { return playerB; }
    public int getStage() { return stage; }
    public GameState getState() { return state; }

    public ClientHandler handlerFor(String u) {
        return playerA.equalsIgnoreCase(u) ? handlerA : handlerB;
    }

    public ClientHandler opponentOf(String u) {
        return playerA.equalsIgnoreCase(u) ? handlerB : handlerA;
    }

    public String opponent(String u) {
        return playerA.equalsIgnoreCase(u) ? playerB : playerA;
    }

    public boolean contains(String u) {
        return u != null && (playerA.equalsIgnoreCase(u) || playerB.equalsIgnoreCase(u));
    }
}
