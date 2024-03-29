package net.dobbs.blockman.mixin;

import net.dobbs.blockman.tiles.TileManager;
import net.dobbs.blockman.util.PlayerAccess;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.io.IOException;
import java.util.ArrayList;

@Mixin(PlayerEntity.class)
public class PlayerTileMixin implements PlayerAccess {
    TileManager playersTiles = new TileManager();
    @Override
    public boolean doesPlayerOwn(double x, double y, double z)
    {
        return playersTiles.isTileOwned(x, y, z);
    }

    public void addTile(double x, double y, double z)
    {
        playersTiles.addTile(x, y, z);
    }

    public void addTile(int chunkX, int chunkZ, int tileNum)
    {
        playersTiles.addTile(chunkX, chunkZ, tileNum);
    }

    public byte[] serializeTileMap() throws IOException {
        return playersTiles.serializeTileMap();
    }

    public void deSerializeTileMap(byte[] bytes) throws IOException, ClassNotFoundException {
        playersTiles.deSerializeTileMap(bytes);
    }

    public boolean containsOwned(int chunkX, int chunkZ)
    {
        return playersTiles.containsOwned(chunkX, chunkZ);
    }

    public ArrayList<Integer> getTilesInChunk(int chunkX, int chunkY)
    {
        return playersTiles.getTilesInChunk(chunkX, chunkY);
    }

    public void increaseAvailableTiles(int num)
    {
        playersTiles.increaseAvailableTiles(num);
    }

    public void decreaseAvailableTiles(int num)
    {
        playersTiles.decreaseAvailableTiles(num);
    }

    public int getAvailableTiles()
    {
        return playersTiles.getAvailableTiles();
    }

    public void setAvailableTiles(int num)
    {
        playersTiles.setAvailableTiles(num);
    }
}
