package PvZ2.APproject.server;

import PvZ2.APproject.server.repositories.UserRepository;
import PvZ2.APproject.server.services.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static final int PORT = 5000;

    private static final Map<String, ClientHandler> ONLINE = new ConcurrentHashMap<>();

    private final UserRepository users = new UserRepository(Path.of("server-data", "users.dat"));
    private final AuthService auth = new AuthService(users);
    private final MatchmakingService matchmaking = new MatchmakingService(users);
    private final GameService game = new GameService(matchmaking);
    private final ReactionService reaction = new ReactionService(matchmaking);
    private final LeaderboardService leaderboard = new LeaderboardService(users);
    private final ScoreService score = new ScoreService(users);

    public static ClientHandler online(String username) {
        return username == null ? null : ONLINE.get(username.toLowerCase());
    }

    static void online(ClientHandler ch) {
        if (ch.getUsername() != null) ONLINE.put(ch.getUsername().toLowerCase(), ch);
    }

    static void offline(ClientHandler ch) {
        if (ch.getUsername() != null) ONLINE.remove(ch.getUsername().toLowerCase(), ch);
    }

    public AuthService auth() {
        return auth;
    }

    public MatchmakingService matchmaking() {
        return matchmaking;
    }

    public GameService game() {
        return game;
    }

    public ReactionService reaction() {
        return reaction;
    }

    public LeaderboardService leaderboard() {
        return leaderboard;
    }

    public ScoreService score() {
        return score;
    }

    public static void main(String[] args){
        Server server = new Server();
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            System.out.println("Waiting for clients...");
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");

                ClientHandler clientHandler = new ClientHandler(clientSocket, server);

                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
