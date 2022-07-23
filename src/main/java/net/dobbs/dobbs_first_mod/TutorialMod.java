package net.dobbs.dobbs_first_mod;

import io.netty.channel.ChannelHandler;
import net.dobbs.dobbs_first_mod.item.ModItems;
import net.dobbs.dobbs_first_mod.tiles.TileManager;
import net.dobbs.dobbs_first_mod.util.PlayerAccess;
import net.dobbs.dobbs_first_mod.util.PlayerMoveCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;

public class TutorialMod implements ModInitializer {
	//test comment
	public static final String MOD_ID = "dobbs_first_mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final DecimalFormat rounder = new DecimalFormat("0.00");

	public static final Identifier blockManIdentifier = new Identifier("blockman");
	@Override
	public void onInitialize() {

		ModItems.registerModItems();

		//Rendering Variables
		var positionString = new Object(){String s;};
		var chunkString = new Object(){String s;};
		var tileString = new Object(){String s;};
		var ownedString = new Object(){String s; int color;};

		//Rendering
		HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
			TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
			renderer.draw(matrixStack, positionString.s, 5, 330, 0xffffff);
			renderer.draw(matrixStack, chunkString.s, 5, 340, 0xffffff);
			renderer.draw(matrixStack, tileString.s, 5, 350, 0xffffff);
			renderer.draw(matrixStack, "Owned?: ", 5, 360, 0xffffff);
			renderer.draw(matrixStack, ownedString.s, 47, 360, ownedString.color);
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
				positionString.s = "Position: " + rounder.format(player.getPos().x) + ", " + rounder.format(player.getPos().y) + ", " + rounder.format(player.getPos().z);
				chunkString.s = "Chunk: " + TileManager.makeStringKey(player.getPos().x, player.getPos().z);
				tileString.s = "Tile: " + TileManager.getTileNumber(player.getPos().x, player.getPos().y, player.getPos().z);

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

		//Server Networking
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			String path = server.getSavePath(WorldSavePath.PLAYERDATA).toString();
			String uniqueID = handler.player.getUuidAsString();

			path += uniqueID + ".blockman";

			File file = new File(path);

			if(file.isFile() == true)
			{
				LOGGER.info(handler.player.getEntityName() + " has data");
				//Load data to into tileManager

				((PlayerAccess)handler.player).loadTileMap(path);
			}
			else
			{
				LOGGER.info(handler.player.getEntityName() + " is connecting for the first time!");

				//Add first tile to tileManager
				((PlayerAccess)handler.player).addTile(handler.player.getX(), handler.player.getY(), handler.player.getZ());
				LOGGER.info("Added first tile for " + handler.player.getEntityName());
			}

			//Send HashMap to Client
			PacketByteBuf buffer = PacketByteBufs.create();
			byte [] serializedTileMap;
			try {
				serializedTileMap = ((PlayerAccess)handler.player).serializeTileMap();
			} catch (IOException e) {
				throw new RuntimeException(e);
			};

			buffer.writeByteArray(serializedTileMap);
			ServerPlayNetworking.send((ServerPlayerEntity) handler.player, blockManIdentifier,buffer);

			LOGGER.info("Sent Packet: " + serializedTileMap.toString());
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			LOGGER.info(handler.player.getEntityName() + " has disconnected");

			String path = server.getSavePath(WorldSavePath.PLAYERDATA).toString();
			String uniqueID = handler.player.getUuidAsString();

			path +=  "\\" + uniqueID + ".blockman";

			byte[] serializedTileMap;
			try {
				serializedTileMap = ((PlayerAccess)handler.player).serializeTileMap();
			} catch (IOException e) {
				throw new RuntimeException(e);
			};

			try {
				Files.write(Path.of(path), serializedTileMap);
				LOGGER.info("Wrote data file to " + Path.of(path).toString());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(blockManIdentifier,(client, handler, buf, responseSender) -> {
			byte[] receivedBytes = buf.readByteArray();
			LOGGER.info("Received Packet" + receivedBytes.toString());

			client.execute(() -> {
				try {
					((PlayerAccess)client.player).deSerializeTileMap(receivedBytes);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			});
		});
	}
}
