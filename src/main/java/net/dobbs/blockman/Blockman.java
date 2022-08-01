package net.dobbs.blockman;

import net.dobbs.blockman.events.*;
import net.dobbs.blockman.item.ModItems;
import net.dobbs.blockman.tiles.TileManager;
import net.dobbs.blockman.util.PlayerAccess;
import net.dobbs.blockman.util.PlayerMoveCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class Blockman implements ModInitializer {
	public static final String MOD_ID = "blockman";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		TileRenderer.init();
		ServerEvents.init();
		ClientEvents.init();
		PlayerCollision.init();
		HUDRenderer.getInstance();
	}
}
