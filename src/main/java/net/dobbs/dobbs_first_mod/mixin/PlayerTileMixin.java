package net.dobbs.dobbs_first_mod.mixin;

import net.dobbs.dobbs_first_mod.tiles.TileManager;
import net.dobbs.dobbs_first_mod.util.PlayerAccess;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Session;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Mixin(PlayerEntity.class)
public class PlayerTileMixin implements PlayerAccess {
    TileManager playersTiles = new TileManager();
    @Override
    public boolean doesPlayerOwn(double x, double y, double z)
    {
        return playersTiles.isOwned(x, y, z);
    }

    public void addTile(double x, double y, double z)
    {
        playersTiles.addTile(x, y, z);
    }

    public void loadTileMap(String path)
    {
        playersTiles.loadTiles(path);
    }
    public byte[] serializeTileMap() throws IOException {
        return playersTiles.serializeTileMap();
    }

    public void deSerializeTileMap(byte[] bytes) throws IOException, ClassNotFoundException {
        playersTiles.deSerializeTileMap(bytes);
    }
}
