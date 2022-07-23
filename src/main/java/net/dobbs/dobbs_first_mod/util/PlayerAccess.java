package net.dobbs.dobbs_first_mod.util;

import java.io.IOException;

public interface PlayerAccess {
    boolean doesPlayerOwn(double x, double y, double z);
    void addTile(double x, double y, double z);
    void loadTileMap(String path);
    byte[] serializeTileMap() throws IOException;
    void deSerializeTileMap(byte [] bytes) throws IOException, ClassNotFoundException;

}
