package PvZ2.APproject.server.repositories;

import PvZ2.APproject.server.models.ServerUser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private final Map<String, ServerUser> users = new ConcurrentHashMap<>();
    private final Path file;

    public UserRepository(Path file) {
        this.file = file;
        load();
    }

    public synchronized boolean exists(String username) {
        return username != null && users.containsKey(username.toLowerCase());
    }

    public synchronized ServerUser find(String username) {
        return username == null ? null : users.get(username.toLowerCase());
    }

    public synchronized boolean save(ServerUser user) {
        if (user == null || user.getUsername() == null || user.getUsername().isBlank()) return false;
        if (exists(user.getUsername())) return false;
        users.put(user.getUsername().toLowerCase(), user);
        persist();
        return true;
    }

    public synchronized Collection<ServerUser> all() {
        return new ArrayList<>(users.values());
    }

    public synchronized void update(ServerUser user) {
        if (user == null || user.getUsername() == null || user.getUsername().isBlank()) return;
        users.put(user.getUsername().toLowerCase(), user);
        persist();
    }

    public synchronized boolean rename(String oldUsername, String newUsername) {
        if (oldUsername == null || newUsername == null || newUsername.isBlank()) return false;
        String oldKey = normalize(oldUsername);
        String newKey = normalize(newUsername);
        if (!users.containsKey(oldKey) || (users.containsKey(newKey) && !oldKey.equals(newKey))) return false;

        ServerUser user = users.remove(oldKey);
        user.setUsername(newUsername);
        users.put(newKey, user);
        persist();
        return true;
    }

    private void load() {
        if (!Files.exists(file)) return;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            Object o = in.readObject();
            if (o instanceof Collection<?>) for (Object item : (Collection<?>) o)
                if (item instanceof ServerUser u) users.put(u.getUsername().toLowerCase(), u);
        } catch (Exception e) {
            System.err.println("Could not load server users: " + e.getMessage());
        }
    }

    private void persist() {
        try {
            Files.createDirectories(file.toAbsolutePath().getParent());
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
                out.writeObject(new ArrayList<>(users.values()));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save server users", e);
        }
    }

    private static String normalize(String username) {
        return username.trim().toLowerCase();
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
