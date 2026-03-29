package com.github.theredbrain.mobbrainadditions;

import com.github.theredbrain.mobbrainadditions.config.ServerConfig;
import com.github.theredbrain.mobbrainadditions.registry.BlockRegistry;
import com.github.theredbrain.mobbrainadditions.registry.EntityRegistry;
import com.github.theredbrain.mobbrainadditions.registry.ServerPacketRegistry;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobBrainAdditions implements ModInitializer {
	public static final String MOD_ID = "mob_brain_additions";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ServerConfig SERVER_CONFIG;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerConfig::new);

		BlockRegistry.init();
		EntityRegistry.init();
		ServerPacketRegistry.init();
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}

}