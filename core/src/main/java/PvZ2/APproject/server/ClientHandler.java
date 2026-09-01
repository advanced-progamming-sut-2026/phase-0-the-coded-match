package PvZ2.APproject.server;

import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread{

    private final Socket socket;
    private final Server server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile String username;
    private volatile String sessionId;


    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String u) {
        if (username != null) {
            Server.offline(this);
        }
        username = u;
        if (u != null) {
            Server.online(this);
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String id) {
        sessionId = id;
    }

    public synchronized void push(Response response) {
        try {
            output.writeObject(response);
            output.flush();
        } catch (IOException ignored) {
        }
    }


    @Override
    public void run(){
        try{
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();

            input = new ObjectInputStream(socket.getInputStream());

            while(true){

                Request request = (Request) input.readObject();
                Response response = assignRequest(request);
                if (response != null) {
                    push(response);
                }
            }
        }catch (Exception e) {
            System.out.println("Client disconnected: " + username);
        } finally {
            Server.offline(this);
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private Response assignRequest(Request request){
        if (request == null || request.getType() == null) return Response.error(request == null ? "" : "", "Invalid request");
        return switch (request.getType()){
            case REGISTER -> server.auth().register(request);
            case LOGIN -> server.auth().login(request, this);
            case LOGOUT -> server.auth().logout(request, this);
            case GET_PROFILE -> server.auth().profile(request, this);
            case UPDATE_PROFILE -> server.auth().updateProfile(request, this);
            case SET_SECURITY_QUESTION -> server.auth().setSecurityQuestion(request, this);
            case FORGOT_PASSWORD -> server.auth().forgotPassword(request);
            case VERIFY_SECURITY_ANSWER -> server.auth().verifySecurityAnswer(request);
            case RESET_PASSWORD -> server.auth().resetPassword(request);
            case SYNC_USER_STATE -> server.auth().syncState(request, this);
            case FIND_RANDOM_MATCH -> server.matchmaking().random(request, this);
            case FIND_PLAYER -> server.matchmaking().findPlayer(request, this);
            case ACCEPT_MATCH -> server.matchmaking().accept(request, this);
            case REJECT_MATCH -> server.matchmaking().reject(request, this);
            case GAME_ACTION -> server.game().action(request, this);
            case GAME_STATE -> server.game().state(request, this);
            case GAME_OVER -> server.game().finish(request, this);
            case SEND_REACTION -> server.reaction().send(request, this);
            case GET_LEADERBOARD -> server.leaderboard().get(request, this);
            case SUBMIT_SCORE -> server.score().submit(request, this);
            default -> Response.error(request.getRequestId(), "Unsupported request type");
        };
    }
}
