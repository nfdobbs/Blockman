package net.dobbs.blockman;

import net.dobbs.blockman.events.*;
import net.dobbs.blockman.item.ModItems;
import net.dobbs.blockman.util.Config;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Blockman implements ModInitializer {
	public static final String MOD_ID = "blockman";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		Config.getInstance();
		TileRenderer.init();
		ServerEvents.init();
		ClientEvents.init();
		PlayerCollision.init();
		HUDRenderer.getInstance();
	}
}
