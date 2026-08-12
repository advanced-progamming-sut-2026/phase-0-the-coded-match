package PvZ2.APproject.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public final class AssetPaths { //TODO: seems like this class is not necessary
    private AssetPaths() {
    }

    public static Path resolve(String path) {
        Path requested = Path.of(path.replace('\\', '/'));
        String fileName = requested.getFileName().toString();
        Set<Path> candidates = new LinkedHashSet<>();
        candidates.add(requested);
        candidates.add(Path.of("src", "assets", fileName));
        candidates.add(Path.of("assets", fileName));
        addCandidates(candidates, Path.of(System.getProperty("user.dir", ".")), fileName);
        try {
            URI location = AssetPaths.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            if ("file".equalsIgnoreCase(location.getScheme())) {
                addCandidates(candidates, Path.of(location), fileName);
            }
        } catch (Exception ignored) {
        }
        try {
            URL resource = AssetPaths.class.getClassLoader().getResource("assets/" + fileName);
            if (resource != null && "file".equalsIgnoreCase(resource.getProtocol())) {
                candidates.add(Path.of(resource.toURI()));
            }
        } catch (Exception ignored) {
        }
        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                return candidate.toAbsolutePath().normalize();
            }
        }
        Path fallback = Path.of(System.getProperty("user.dir", "."), "src", "assets", fileName)
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(fallback.getParent());
        } catch (IOException ignored) {
        }
        return fallback;
    }

    private static void addCandidates(Set<Path> candidates, Path start, String fileName) {
        Path current = start.toAbsolutePath().normalize();
        if (Files.isRegularFile(current)) {
            current = current.getParent();
        }
        for (int i = 0; current != null && i < 10; i++, current = current.getParent()) {
            candidates.add(current.resolve(Path.of("src", "assets", fileName)));
            candidates.add(current.resolve(Path.of("assets", fileName)));
        }
    }

    public static Reader reader(String path) throws IOException {
        return Files.newBufferedReader(resolve(path), StandardCharsets.UTF_8);
    }

    public static Writer writer(String path) throws IOException {
        Path resolved = resolve(path);
        Files.createDirectories(resolved.getParent());
        return Files.newBufferedWriter(resolved, StandardCharsets.UTF_8);
    }

    public static String readString(String path) throws IOException {
        return Files.readString(resolve(path), StandardCharsets.UTF_8);
    }
}
