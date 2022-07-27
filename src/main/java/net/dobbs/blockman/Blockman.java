package net.dobbs.blockman;

import net.dobbs.blockman.events.ClientEvents;
import net.dobbs.blockman.events.ServerEvents;
import net.dobbs.blockman.events.TileRenderer;
import net.dobbs.blockman.item.ModItems;
import net.dobbs.blockman.tiles.TileManager;
import net.dobbs.blockman.util.PlayerAccess;
import net.dobbs.blockman.util.PlayerMoveCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class Blockman implements ModInitializer {
	//test comment
	public static final String MOD_ID = "blockman";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final DecimalFormat rounder = new DecimalFormat("0.00");

	public static final Identifier blockManIdentifier = new Identifier("blockman");

	//public static final Identifier tileWallTexture = new Identifier("dobbs_first_mod","textures/exp_tile_wall.png");
	@Override
	public void onInitialize() {

		ModItems.registerModItems();

		TileRenderer.init();
		ServerEvents.init();
		ClientEvents.init();

		//Rendering Variables
		var positionString = new Object(){String s;};
		var chunkString = new Object(){String s;};
		var tileString = new Object(){String s;};
		var ownedString = new Object(){String s; int color;};

		var predictString = new Object(){String s;};

		//Rendering
		HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
			TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
			renderer.draw(matrixStack, positionString.s, 5, 330, 0xffffff);
			renderer.draw(matrixStack, chunkString.s, 5, 340, 0xffffff);
			renderer.draw(matrixStack, tileString.s, 5, 350, 0xffffff);
			renderer.draw(matrixStack, "Owned?: ", 5, 360, 0xffffff);
			renderer.draw(matrixStack, ownedString.s, 47, 360, ownedString.color);
			renderer.draw(matrixStack, predictString.s, 5, 370, 0xffffff);
		});

		//Movement
		PlayerMoveCallback.EVENT.register((player, collisionVector) -> {
			World world = player.getWorld();
			Vec3d returnVector = collisionVector;

			double regionX, regionZ = 0;
			double chunkX, chunkZ = 0;

			if(world.isClient != true) {
			}

			if(world.isClient == true){
				int tileNum;
				String chunk;
				positionString.s = "Position: " + rounder.format(player.getPos().x) + ", " + rounder.format(player.getPos().y) + ", " + rounder.format(player.getPos().z);
				chunk = TileManager.makeStringKey(player.getPos().x, player.getPos().z);
				chunkString.s = "Chunk: " + chunk;
				tileNum = TileManager.getTileNumber(player.getPos().x, player.getPos().y, player.getPos().z);
				tileString.s = "Tile: " + tileNum;

				predictString.s = "Predicted: " + TileManager.firstBlock(tileNum, chunk);



				if(((PlayerAccess)player).doesPlayerOwn(player.getX(), player.getY(), player.getZ()) == true)
				{
					ownedString.s = "True";
					ownedString.color = 0x00ff00;
				}
				else
				{
					ownedString.s = "False";
					ownedString.color = 0xff0000;
				}

				//((PlayerAccess)player).addTile(0,0,0);
			}

			//Currently Just Stopping the Player
			//Hard Math Ahead


			return collisionVector;
		});
	}
}