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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class Blockman implements ModInitializer {
	public static final String MOD_ID = "blockman";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final DecimalFormat rounder = new DecimalFormat("0.00");

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



			double regionX, regionZ = 0;
			double chunkX, chunkZ = 0;

			if(world.isClient != true) {

				//System.out.println(((PlayerAccess)player).doesPlayerOwn(player.getX(), player.getY(), player.getZ()));
			}

			if(world.isClient == true){
				int tileNum;
				String chunk;
				positionString.s = "Position: " + rounder.format(player.getPos().x) + ", " + rounder.format(player.getPos().y) + ", " + rounder.format(player.getPos().z);
				chunk = TileManager.makeStringKey(player.getPos().x, player.getPos().z);
				chunkString.s = "Chunk: " + chunk;
				tileNum = TileManager.getTileNumber(player.getPos().x, player.getPos().y, player.getPos().z);
				tileString.s = "Tile: " + tileNum;

				predictString.s = "Collision: " + TileManager.firstBlock(TileManager.getTileNumber(player.getPos().x, player.getPos().y, player.getPos().z), TileManager.makeStringKey(player.getPos().x, player.getPos().z));

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
			}

			//Currently Just Stopping the Player
			//Complex Math Ahead

			Vec3d playerPosition = player.getPos();
			Vec3d returnVector = collisionVector;
			Box playerBox = player.getBoundingBox();

			int tileNum = TileManager.getTileNumber(playerPosition.x, playerPosition.y, playerPosition.z);
			String chunk = TileManager.makeStringKey(playerPosition.x, playerPosition.z);

			Vec3d lowestTileBlock = TileManager.firstBlock(tileNum, chunk);

			double yMovement = collisionVector.y;
			double xMovement = 0.0;
			double zMovement = 0.0;


			((PlayerAccess)player).doesPlayerOwn(playerPosition.x, (playerBox.minY + collisionVector.y) , playerPosition.z);

			if(((PlayerAccess)player).doesPlayerOwn(playerPosition.x, playerPosition.y, playerPosition.z) == true)
			{
				//Checking Y
				if (collisionVector.y < 0)    //Falling
				{
					if (!((PlayerAccess) player).doesPlayerOwn(playerPosition.x, (playerBox.minY + collisionVector.y), playerPosition.z))
					{
						yMovement = lowestTileBlock.y - playerPosition.y;

						if (Math.abs(yMovement) < 1.0E-7) {
							yMovement = 0.0;
						}
					}
				}

				else if (collisionVector.y > 0) //Jumping
				{
					if (!((PlayerAccess) player).doesPlayerOwn(playerPosition.x, (playerBox.maxY + collisionVector.y), playerPosition.z))
					{
						yMovement = (lowestTileBlock.y+4) - playerBox.maxY;

						if (Math.abs(yMovement) < 1.0E-7) {
							yMovement = 0.0;
						}
					}
				}

				//+X Heading East
				//-X Heading West
				//+Z Heading South
				//-Z Heading North


				//System.out.println(playerPosition.y + " " + yMovement);

				returnVector = new Vec3d(collisionVector.x, yMovement, collisionVector.z);

			}

			return returnVector;
		});
	}
}
