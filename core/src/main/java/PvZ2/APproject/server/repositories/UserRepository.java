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
        if (exists(user.getUsername())) return false;
        users.put(user.getUsername().toLowerCase(), user);
        persist();
        return true;
    }

    public synchronized Collection<ServerUser> all() {
        return new ArrayList<>(users.values());
    }

    public synchronized void update(ServerUser user) {
        users.put(user.getUsername().toLowerCase(), user);
        persist();
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

//    public static String hash(String value) {
//        try {
//            byte[] b = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
//            StringBuilder s = new StringBuilder();
//            for (byte x : b) s.append(String.format("%02x", x));
//            return s.toString();
//        } catch (Exception e) {
//            throw new IllegalStateException(e);
//        }
//    }

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
