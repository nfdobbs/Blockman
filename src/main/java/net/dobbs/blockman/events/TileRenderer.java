package net.dobbs.blockman.events;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dobbs.blockman.tiles.TileManager;
import net.dobbs.blockman.util.BlockmanColor;
import net.dobbs.blockman.util.Config;
import net.dobbs.blockman.util.PlayerAccess;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;

public class TileRenderer {
    public static final Identifier tileWallTexture = new Identifier("blockman","textures/exp_tile_wall.png");

    private static BlockmanColor canPurchaseColor;
    private static BlockmanColor noTilesColor;

    public static void init()
    {
        Config config = Config.getInstance();

        canPurchaseColor = config.getCanPurchaseWallColor();
        noTilesColor = config.getNoTilesAvailableWallColor();

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {

            ArrayList<Integer> holder;
            String key;
            double tilesY = 0;
            double camsY = context.camera().getPos().y;

            int renderDist = (int)context.gameRenderer().getViewDistance();


            double maxRenderedY = camsY + renderDist;
            double minRenderedY = camsY - renderDist;

            ClientPlayerEntity player = MinecraftClient.getInstance().player;

            renderDist = renderDist/16;

            int chunkXStart = (int)java.lang.Math.floor(context.camera().getPos().x/16);
            int chunkZStart = (int)java.lang.Math.floor(context.camera().getPos().z/16);

            chunkXStart = chunkXStart - renderDist + 1;
            chunkZStart = chunkZStart - renderDist + 1;

            int chunkLimit = (renderDist * 2) - 1;

            RenderSystem.disableCull();
            RenderSystem.setShaderColor(noTilesColor.getRedFloat(), noTilesColor.getGreenFloat(), noTilesColor.getBlueFloat(), noTilesColor.getAlphaFloat());
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.enableDepthTest();
            RenderSystem.enableTexture();
            RenderSystem.enableBlend();

            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

            //Loops through chunks within render distance for any with tiles
            for (int x = 0; x < chunkLimit; x++)
            {
                for (int z = 0; z < chunkLimit; z++) {
                    if (((PlayerAccess) player).containsOwned(chunkXStart+x, chunkZStart+z) == true) {
                        holder = ((PlayerAccess) player).getTilesInChunk(chunkXStart + x, chunkZStart+z);

                        key = "(" + (chunkXStart+x) + "," + (chunkZStart+z) + ")";

                        for(Integer tileNum : holder)
                        {
                            tilesY = java.lang.Math.floor(tileNum / 16)*4;
                            tilesY = tilesY - 64;

                            if(tilesY >= minRenderedY && tilesY <= maxRenderedY)
                                renderTile(context, bufferBuilder, tileNum, key);
                        }
                    }
                }
            }
            Tessellator.getInstance().draw();
        });
    }

    public static void renderTile(WorldRenderContext context, BufferBuilder bufferBuilder, int tileNum, String chunkCoords)
    {
        float offset = -0.0001f;

        PlayerAccess player = (PlayerAccess) MinecraftClient.getInstance().player;

        MatrixStack stack = context.matrixStack();
        Matrix4f posMatrix = stack.peek().getPositionMatrix();

        Vec3d firstBlock = TileManager.firstBlock(tileNum, chunkCoords);

        Vec3d worldLocation = firstBlock.subtract(context.camera().getPos());

        RenderSystem.setShaderTexture(0, tileWallTexture);

        //North Wall Code
        if(!player.doesPlayerOwn(firstBlock.x, firstBlock.y, firstBlock.z - 1))
        {
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x, (float) worldLocation.y, (float) worldLocation.z - offset).texture(0, 1).next();
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x, (float) worldLocation.y + 4, (float) worldLocation.z - offset).texture(0, 0).next();

            bufferBuilder.vertex(posMatrix, (float) worldLocation.x + 4, (float) worldLocation.y + 4, (float) worldLocation.z - offset).texture(1, 0).next();
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x + 4, (float) worldLocation.y, (float) worldLocation.z - offset).texture(1, 1).next();
        }

        //South Wall Code
        if(!player.doesPlayerOwn(firstBlock.x, firstBlock.y, firstBlock.z + 4))
        {
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x + 4, (float) worldLocation.y, (float) worldLocation.z + 4 + offset).texture(0, 1).next();
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x + 4, (float) worldLocation.y + 4, (float) worldLocation.z + 4 + offset).texture(0, 0).next();

            bufferBuilder.vertex(posMatrix, (float) worldLocation.x, (float) worldLocation.y + 4, (float) worldLocation.z + 4 + offset).texture(1, 0).next();
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x, (float) worldLocation.y, (float) worldLocation.z + 4 + offset).texture(1, 1).next();
        }

        //East Wall Code
        if(!player.doesPlayerOwn(firstBlock.x + 4, firstBlock.y, firstBlock.z))
        {
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x + 4 + offset, (float) worldLocation.y, (float) worldLocation.z).texture(0, 1).next();
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x + 4 + offset, (float) worldLocation.y + 4, (float) worldLocation.z).texture(0, 0).next();

            bufferBuilder.vertex(posMatrix, (float) worldLocation.x + 4 + offset, (float) worldLocation.y + 4, (float) worldLocation.z + 4).texture(1, 0).next();
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x + 4 + offset, (float) worldLocation.y, (float) worldLocation.z + 4).texture(1, 1).next();
        }

        //West Wall Code
        if(!player.doesPlayerOwn(firstBlock.x - 1, firstBlock.y, firstBlock.z))
        {
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x - offset, (float) worldLocation.y, (float) worldLocation.z + 4).texture(0, 1).next();
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x - offset, (float) worldLocation.y + 4, (float) worldLocation.z + 4).texture(0, 0).next();

            bufferBuilder.vertex(posMatrix, (float) worldLocation.x - offset, (float) worldLocation.y + 4, (float) worldLocation.z).texture(1, 0).next();
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x - offset, (float) worldLocation.y, (float) worldLocation.z).texture(1, 1).next();
        }

        //Ceiling Code
        if(!player.doesPlayerOwn(firstBlock.x, firstBlock.y + 4, firstBlock.z))
        {
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x, (float) worldLocation.y + 4 + offset, (float) worldLocation.z).texture(0, 1).next();
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x + 4, (float) worldLocation.y + 4 + offset, (float) worldLocation.z).texture(0, 0).next();

            bufferBuilder.vertex(posMatrix, (float) worldLocation.x + 4, (float) worldLocation.y + 4 + offset, (float) worldLocation.z + 4).texture(1, 0).next();
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x, (float) worldLocation.y + 4 + offset, (float) worldLocation.z + 4 - offset).texture(1, 1).next();
        }

        //Floor Code
        if(!player.doesPlayerOwn(firstBlock.x, firstBlock.y - 1, firstBlock.z))
        {
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x, (float) worldLocation.y - offset, (float) worldLocation.z).texture(0, 1).next();
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x + 4, (float) worldLocation.y - offset, (float) worldLocation.z).texture(0, 0).next();

            bufferBuilder.vertex(posMatrix, (float) worldLocation.x + 4, (float) worldLocation.y - offset, (float) worldLocation.z + 4).texture(1, 0).next();
            bufferBuilder.vertex(posMatrix, (float) worldLocation.x, (float) worldLocation.y - offset, (float) worldLocation.z + 4 - offset).texture(1, 1).next();
        }
    }
}
