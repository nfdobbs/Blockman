package net.dobbs.blockman.events;

import net.dobbs.blockman.tiles.TileManager;
import net.dobbs.blockman.util.PlayerAccess;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.World;

import java.text.DecimalFormat;

public final class HUDRenderer {

    private static final DecimalFormat rounder = new DecimalFormat("0.00");
    private static HUDRenderer instance = null;
    public String positionLine = "";
    public String chunkLine = "";
    public String tileLine = "";
    public String tileOwnedLine = "";
    public int tileOwnedColor;
    public String predictedLine = "";

    public String availableTilesLine = "";

    private HUDRenderer()
    {
        init();
    }

    public static HUDRenderer getInstance()
    {
        if(instance == null)
            instance = new HUDRenderer();

        return instance;
    }

    private void init()
    {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            var positionString = new Object(){String s = positionLine;};
            var chunkString = new Object(){String s = chunkLine;};
            var tileString = new Object(){String s = tileLine;};
            var ownedString = new Object(){String s = tileOwnedLine; int color;};
            var predictString = new Object(){String s = predictedLine;};

            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            renderer.draw(matrixStack, positionString.s, 5, 330, 0xffffff);
            renderer.draw(matrixStack, chunkString.s, 5, 340, 0xffffff);
            renderer.draw(matrixStack, tileString.s, 5, 350, 0xffffff);
            renderer.draw(matrixStack, "Owned?: ", 5, 360, 0xffffff);
            renderer.draw(matrixStack, ownedString.s, 47, 360, tileOwnedColor);
            renderer.draw(matrixStack, predictString.s, 5, 370, 0xffffff);
            renderer.draw(matrixStack, "Tiles Available:" + availableTilesLine, 5, 380, 0xffffff);
        });
    }
}
