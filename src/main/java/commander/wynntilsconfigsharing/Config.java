package commander.wynntilsconfigsharing;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Gson gson = new Gson();
    private static Path path;
    public String currentConfig = "";

    public static Config load(Path configPath) throws IOException {
        Config config;
        if (Files.isRegularFile(configPath)) {
            config = gson.fromJson(new String(Files.readAllBytes(configPath)), Config.class);
        } else {
            config = new Config();
            Files.createDirectories(configPath.getParent());
            Files.createFile(configPath);
            Files.write(configPath, gson.toJson(config).getBytes());
        }
        config.setPath(configPath);
        return config;
    }

    public void setPath(Path newPath) {
        path = newPath;
    }

    public void save() throws IOException {
        Files.write(path, gson.toJson(this).getBytes());
    }
}
