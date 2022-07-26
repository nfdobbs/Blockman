package net.dobbs.dobbs_first_mod;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.channel.ChannelHandler;
import net.dobbs.dobbs_first_mod.item.ModItems;
import net.dobbs.dobbs_first_mod.tiles.TileManager;
import net.dobbs.dobbs_first_mod.util.PlayerAccess;
import net.dobbs.dobbs_first_mod.util.PlayerMoveCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.GameEventDebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;

public class TutorialMod implements ModInitializer {
	//test comment
	public static final String MOD_ID = "dobbs_first_mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final DecimalFormat rounder = new DecimalFormat("0.00");

	public static final Identifier blockManIdentifier = new Identifier("blockman");

	public static final Identifier tileWallTexture = new Identifier("dobbs_first_mod","textures/tile_wall3.png");
	@Override
	public void onInitialize() {

		ModItems.registerModItems();

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

		//Server Connect Event
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			String path = server.getSavePath(WorldSavePath.PLAYERDATA).toString();
			String uniqueID = handler.player.getUuidAsString();

			path += "\\" + uniqueID + ".blockman";

			LOGGER.info(path);
			byte [] serializedTileMap;
			File file = new File(path);

			//Has Player Data
			if(file.isFile() == true)
			{
				LOGGER.info(handler.player.getEntityName() + " has data");
				//Load data to into tileManager

				try {
					serializedTileMap = Files.readAllBytes(Path.of(path));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				try {
					((PlayerAccess)handler.player).deSerializeTileMap(serializedTileMap);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}

			}
			//New Player Connecting
			else
			{
				LOGGER.info(handler.player.getEntityName() + " is connecting for the first time!");

				//Add first tile to tileManager
				((PlayerAccess)handler.player).addTile(handler.player.getX(), handler.player.getY(), handler.player.getZ());
				LOGGER.info("Added first tile for " + handler.player.getEntityName());

				try {
					serializedTileMap = ((PlayerAccess)handler.player).serializeTileMap();
				} catch (IOException e) {
					throw new RuntimeException(e);
				};
			}

			//Send HashMap to Client
			PacketByteBuf buffer = PacketByteBufs.create();
			buffer.writeByteArray(serializedTileMap);
			ServerPlayNetworking.send((ServerPlayerEntity) handler.player, blockManIdentifier,buffer);

			LOGGER.info("Sent Packet: " + serializedTileMap.toString());
		});

		//Server Disconnect Event
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

		//Client Packet Received Event
		ClientPlayNetworking.registerGlobalReceiver(blockManIdentifier,(client, handler, buf, responseSender) -> {
			byte[] receivedBytes = buf.readByteArray();
			LOGGER.info("Received Packet: " + receivedBytes.toString());

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

		//World Rendering
		WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {

			RenderSystem.disableCull();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.enableDepthTest();
			RenderSystem.enableTexture();
			RenderSystem.enableBlend();

			//RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

			//RenderSystem.polygonOffset(-3.0F, -3.0F);
			//RenderSystem.enablePolygonOffset();

			BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			renderTile(context, bufferBuilder, 0, "(0,0)");
			//renderTile(context, bufferBuilder,16, "(0,0)");

			/*ClientPlayerEntity player = MinecraftClient.getInstance().player;

			RenderSystem.disableCull();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.enableDepthTest();
			RenderSystem.enableTexture();
			RenderSystem.enableBlend();

			MatrixStack stack = context.matrixStack();
			Matrix4f posMatrix = stack.peek().getPositionMatrix();

			Vec3d worldLocation = new Vec3d(0, -63, 0).subtract(context.camera().getPos());




			RenderSystem.setShaderTexture(0, tileWallTexture);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

			//North Code for Future
			bufferBuilder.vertex(posMatrix, (float)worldLocation.x, (float)worldLocation.y, (float)worldLocation.z).texture(0,1).next();
			bufferBuilder.vertex(posMatrix, (float)worldLocation.x, (float)worldLocation.y+4, (float)worldLocation.z).texture(0, 0).next();

			bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4, (float)worldLocation.y+4, (float)worldLocation.z).texture(1,0).next();
			bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4, (float)worldLocation.y, (float)worldLocation.z).texture(1,1).next();



			Tessellator.getInstance().draw();

			RenderSystem.disableDepthTest(); */

			Tessellator.getInstance().draw();
		});

	}

	public static void renderTile(WorldRenderContext context, BufferBuilder bufferBuilder, int tileNum, String chunkCoords)
	{
		float offset = -0.0001f;
		ClientPlayerEntity player = MinecraftClient.getInstance().player;

		MatrixStack stack = context.matrixStack();
		Matrix4f posMatrix = stack.peek().getPositionMatrix();

		Vec3d worldLocation = TileManager.firstBlock(tileNum, chunkCoords).subtract(context.camera().getPos());

		RenderSystem.setShaderTexture(0, tileWallTexture);

		//North Wall Code
		bufferBuilder.vertex(posMatrix, (float)worldLocation.x, (float)worldLocation.y, (float)worldLocation.z - offset).texture(0,1).next();
		bufferBuilder.vertex(posMatrix, (float)worldLocation.x, (float)worldLocation.y+4, (float)worldLocation.z - offset).texture(0, 0).next();

		bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4, (float)worldLocation.y+4, (float)worldLocation.z - offset).texture(1,0).next();
		bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4, (float)worldLocation.y, (float)worldLocation.z - offset).texture(1,1).next();

		//South Wall Code
		bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4, (float)worldLocation.y, (float)worldLocation.z+4 + offset).texture(0,1).next();
		bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4, (float)worldLocation.y+4, (float)worldLocation.z+4 + offset).texture(0, 0).next();

		bufferBuilder.vertex(posMatrix, (float)worldLocation.x, (float)worldLocation.y+4, (float)worldLocation.z+4 + offset).texture(1,0).next();
		bufferBuilder.vertex(posMatrix, (float)worldLocation.x, (float)worldLocation.y, (float)worldLocation.z+4 + offset).texture(1,1).next();

		//East Wall Code
		bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4 + offset, (float)worldLocation.y, (float)worldLocation.z).texture(0,1).next();
		bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4 + offset, (float)worldLocation.y+4, (float)worldLocation.z).texture(0, 0).next();

		bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4 + offset, (float)worldLocation.y+4, (float)worldLocation.z+4).texture(1,0).next();
		bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4 + offset, (float)worldLocation.y, (float)worldLocation.z+4).texture(1,1).next();

		//West Wall Code
		bufferBuilder.vertex(posMatrix, (float)worldLocation.x - offset, (float)worldLocation.y, (float)worldLocation.z+4).texture(0,1).next();
		bufferBuilder.vertex(posMatrix, (float)worldLocation.x - offset, (float)worldLocation.y+4, (float)worldLocation.z+4).texture(0, 0).next();

		bufferBuilder.vertex(posMatrix, (float)worldLocation.x - offset, (float)worldLocation.y+4, (float)worldLocation.z).texture(1,0).next();
		bufferBuilder.vertex(posMatrix, (float)worldLocation.x - offset, (float)worldLocation.y, (float)worldLocation.z).texture(1,1).next();
	}

}
