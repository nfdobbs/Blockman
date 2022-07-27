package net.dobbs.dobbs_first_mod.events;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dobbs.dobbs_first_mod.tiles.TileManager;
import net.dobbs.dobbs_first_mod.util.PlayerAccess;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class TileRenderer {
    public static final Identifier tileWallTexture = new Identifier("dobbs_first_mod","textures/exp_tile_wall.png");

    public static void init()
    {
        WorldRenderEvents.BEFORE_ENTITIES.register(context -> {

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
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.enableDepthTest();
            RenderSystem.enableTexture();
            RenderSystem.enableBlend();

            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

            for (int x = 0; x < chunkLimit; x++)
            {
                for (int z = 0; z < chunkLimit; z++) {
                    if (((PlayerAccess) player).containsOwned(chunkXStart+x, chunkZStart+z) == true) {
                        holder = ((PlayerAccess) player).getTilesInChunk(chunkXStart + x, chunkZStart+z);
                        //System.out.println("Found");

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

        //Ceiling Code
        bufferBuilder.vertex(posMatrix, (float)worldLocation.x, (float)worldLocation.y+4 + offset, (float)worldLocation.z).texture(0,1).next();
        bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4, (float)worldLocation.y+4 + offset, (float)worldLocation.z).texture(0, 0).next();

        bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4, (float)worldLocation.y+4 + offset, (float)worldLocation.z+4).texture(1,0).next();
        bufferBuilder.vertex(posMatrix, (float)worldLocation.x, (float)worldLocation.y+4 + offset, (float)worldLocation.z+4 - offset).texture(1,1).next();

        //Floor Code
        bufferBuilder.vertex(posMatrix, (float)worldLocation.x, (float)worldLocation.y - offset, (float)worldLocation.z).texture(0,1).next();
        bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4, (float)worldLocation.y - offset, (float)worldLocation.z).texture(0, 0).next();

        bufferBuilder.vertex(posMatrix, (float)worldLocation.x+4, (float)worldLocation.y - offset, (float)worldLocation.z+4).texture(1,0).next();
        bufferBuilder.vertex(posMatrix, (float)worldLocation.x, (float)worldLocation.y - offset, (float)worldLocation.z+4 - offset).texture(1,1).next();
    }
}
