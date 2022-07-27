package net.dobbs.blockman.util;

import java.io.IOException;
import java.util.ArrayList;

public interface PlayerAccess {
    boolean doesPlayerOwn(double x, double y, double z);
    void addTile(double x, double y, double z);
    void loadTileMap(String path);
    byte[] serializeTileMap() throws IOException;
    void deSerializeTileMap(byte [] bytes) throws IOException, ClassNotFoundException;
    boolean containsOwned(int chunkX, int chunkZ);
    ArrayList<Integer> getTilesInChunk(int chunkX, int chunkZ);
}
