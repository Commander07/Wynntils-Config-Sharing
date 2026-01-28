package commander.wynntilsconfigsharing;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class WynntilsConfigSharing implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("wynntils-config-sharing");
	private static Config config;

	@Override
	public void onInitialize() {
		if (!FabricLoader.getInstance().isModLoaded("wynntils"))
			return;
		UUID uuid = MinecraftClient.getInstance().getSession().getUuidOrNull();
		Path wynntils = FabricLoader.getInstance().getConfigDir().resolve("wynntils-config-sharing/wynntils.json");
		Path configPath = FabricLoader.getInstance().getConfigDir().resolve("wynntils-config-sharing/config.json");

		try {
			config = Config.load(configPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (uuid == null) {
			LOGGER.error("Could not get player UUID!");
			return;
		}
		if (!Files.isRegularFile(wynntils) || !Files.isReadable(wynntils)) {return;}

		Path toBeOverwritten = FabricLoader.getInstance().getGameDir().resolve("wynntils/config/" + uuid.toString().replace("-", "") + ".conf.json");

		try {
			byte[] bytes = Files.readAllBytes(wynntils);
			String hash = DigestUtils.md5Hex(bytes);

			if (config.currentConfig.equals(hash)) {return;}
			config.currentConfig = hash;
			config.save();

			if (!Files.isRegularFile(toBeOverwritten)) {
				Files.createDirectories(toBeOverwritten.getParent());
				Files.createFile(toBeOverwritten);
			}
            byte[] backup = Files.readAllBytes(toBeOverwritten);
			Files.write(toBeOverwritten, bytes);
            Files.write(toBeOverwritten.resolveSibling(
                    toBeOverwritten.getFileName() + ".bak"
            ), backup);
            LOGGER.info("Overwrote: {}", toBeOverwritten);
        } catch (IOException e) {
			LOGGER.error("Failed to overwrite config!");
		}
	}
}